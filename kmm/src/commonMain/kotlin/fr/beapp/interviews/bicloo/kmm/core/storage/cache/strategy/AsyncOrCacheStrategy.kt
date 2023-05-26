package fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy

import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheAwareWrapper
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStrategy
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class AsyncOrCacheStrategy(override val cacheManager: CacheManager, override val json: Json) :
	CacheStrategy(cacheManager, json) {

	override fun <T> applyCacheAwareStrategy(
		asyncBlock: suspend () -> T,
		key: String,
		serializer: KSerializer<T>,
		ttlValue: Long?,
		keepExpiredCache: Boolean
	): Flow<CacheAwareWrapper<T>> {
		return invokeAsync(asyncBlock, key, serializer)
			.catch { e ->
				SharedLogger.warn("Failed to load async data for '$key'", e)
				emitAll(cacheManager.fetchCacheDataFlow(key, serializer, keepExpiredCache, ttlValue))
			}
	}
}
