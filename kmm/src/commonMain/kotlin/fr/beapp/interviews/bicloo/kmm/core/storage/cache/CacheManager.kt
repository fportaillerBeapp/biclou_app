package fr.beapp.interviews.bicloo.kmm.core.storage.cache

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.StrategyType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal typealias AsyncBlock<T> = suspend () -> T

internal class CacheManager(private val storage: CacheStorage? = null, private val json: Json) {

	private fun <T> isCacheValid(
		cacheWrapper: CacheWrapper<T>,
		keepExpiredCache: Boolean,
		ttlValue: Long?
	) = ttlValue == null || (keepExpiredCache || Clock.System.now().toEpochMilliseconds() < cacheWrapper.cachedDate + ttlValue)

	suspend fun <T> storeCacheData(key: String, value: T?, serializer: KSerializer<T>) {
		storage?.let {
			val sessionKey = buildSessionKey(key)
			if (value != null) {
				val cacheWrapper = CacheWrapper(value, Clock.System.now().toEpochMilliseconds())
				it.write(sessionKey, json.encodeToString(CacheWrapper.serializer(serializer), cacheWrapper))
			} else invalidate(sessionKey)
		}
	}

	fun <T> fetchCacheDataFlow(
		key: String,
		serializer: KSerializer<T>,
		keepExpiredCache: Boolean,
		ttlValue: Long?
	): Flow<CacheAwareWrapper<T>> {
		return flow {
			val sessionKey = buildSessionKey(key)
			SharedLogger.debug("Looking in cache for $sessionKey with ttl $ttlValue")

			storage?.read(sessionKey)?.let {
				val cacheWrapper = json.decodeFromString(CacheWrapper.serializer(serializer), it)
				if (isCacheValid(cacheWrapper, keepExpiredCache, ttlValue)) {
					SharedLogger.trace("Cache was found and considered valid for $sessionKey")
					emit(CacheAwareWrapper(true, cacheWrapper.data))
				} else {
					SharedLogger.trace("Cache was not found or invalid for $sessionKey")
					throw EmptyCacheException()
				}
			} ?: run {
				throw EmptyCacheException()
			}
		}
	}

	suspend fun <T : Any> storeData(key: String, value: T?, serializer: KSerializer<T>) {
		storage?.let {
			val sessionKey = buildSessionKey(key)
			if (value != null) {
				it.write(sessionKey, json.encodeToString(serializer, value))
			} else invalidate(sessionKey)
		}
	}

	suspend fun <T> fetchData(key: String, serializer: KSerializer<T>): T? {
		val sessionKey = buildSessionKey(key)
		return storage?.read(sessionKey)?.let {
			json.decodeFromString(serializer, it)
		}
	}

	suspend fun invalidate(key: String) {
		storage?.clear(buildSessionKey(key))
	}


	suspend fun deleteAll() {
		storage?.clear()
	}

	fun <T> from(key: String): StrategyBuilder<T> {
		return StrategyBuilder(key = key)
	}

	private fun buildSessionKey(key: String) = key

	class StrategyBuilder<T>(private val key: String) {

		companion object {
			const val TTL_HOUR = 60 * 60 * 1_000L
			const val TTL_DAY = 24 * 60 * 60 * 1_000L
			const val TTL_WEEK = 7 * 24 * 60 * 60 * 1_000L
			const val TTL_MONTH = 31 * 24 * 60 * 60 * 1_000L
			const val TTL_YEAR = 365 * 24 * 60 * 60 * 1_000L
		}

		private lateinit var serializer: KSerializer<T>

		private var asyncBlock: (suspend () -> T)? = null
		private var strategy: CacheStrategy? = null
		private var ttlValue: Long? = null
		private var keepExpiredCache: Boolean = false

		/**
		 * The [AsyncBlock] to use for async operations.
		 */
		fun withAsync(asyncBlock: AsyncBlock<T>): StrategyBuilder<T> {
			this.asyncBlock = asyncBlock
			return this
		}

		fun withStrategy(strategyType: StrategyType): StrategyBuilder<T> {
			this.strategy = strategyType.getStrategy()
			return this
		}

		fun withTtl(ttlValue: Long?): StrategyBuilder<T> {
			this.ttlValue = ttlValue
			return this
		}

		fun withExpiredCache(keepExpiredCache: Boolean): StrategyBuilder<T> {
			this.keepExpiredCache = keepExpiredCache
			return this
		}

		fun withSerializer(serializer: KSerializer<T>): StrategyBuilder<T> {
			this.serializer = serializer
			return this
		}

		fun execute(): Flow<T> {
			return strategy!!.applyStrategy(asyncBlock!!, key, serializer, ttlValue, keepExpiredCache)
		}

		fun executeCacheAware(): Flow<CacheAwareWrapper<T>> {
			return strategy!!.applyCacheAwareStrategy(asyncBlock!!, key, serializer, ttlValue, keepExpiredCache)
		}
	}
}
