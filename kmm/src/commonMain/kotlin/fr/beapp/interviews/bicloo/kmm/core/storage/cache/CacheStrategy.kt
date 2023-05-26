package fr.beapp.interviews.bicloo.kmm.core.storage.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal abstract class CacheStrategy(open val cacheManager: CacheManager, open val json: Json) {

	fun <T> invokeAsync(
        asyncBlock: AsyncBlock<T>,
        key: String,
        serializer: KSerializer<T>
	): Flow<CacheAwareWrapper<T>> = flow {
		val asyncData = asyncBlock.invoke()
		cacheManager.storeCacheData(key, asyncData, serializer)
		emit(CacheAwareWrapper(false, asyncData))
	}

	fun <T> applyStrategy(
		asyncBlock: suspend () -> T,
		key: String,
		serializer: KSerializer<T>,
		ttlValue: Long?,
		keepExpiredCache: Boolean
	): Flow<T> {
		return applyCacheAwareStrategy(asyncBlock, key, serializer, ttlValue, keepExpiredCache).map { it.data }
	}

	abstract fun <T> applyCacheAwareStrategy(
		asyncBlock: suspend () -> T,
		key: String,
		serializer: KSerializer<T>,
		ttlValue: Long?,
		keepExpiredCache: Boolean
	): Flow<CacheAwareWrapper<T>>

}
