package fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy

import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheAwareWrapper
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class NoCacheStrategy(override val cacheManager: CacheManager, override val json: Json) : CacheStrategy(cacheManager, json) {

	override fun <T> applyCacheAwareStrategy(
		asyncBlock: suspend () -> T,
		key: String,
		serializer: KSerializer<T>,
		ttlValue: Long?,
		keepExpiredCache: Boolean
	): Flow<CacheAwareWrapper<T>> {
		return flow {
			val asyncData = asyncBlock.invoke()
			emit(CacheAwareWrapper(false, asyncData))
		}
	}
}
