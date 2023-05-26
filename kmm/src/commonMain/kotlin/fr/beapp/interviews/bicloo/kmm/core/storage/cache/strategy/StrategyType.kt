package fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy

import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStrategy
import fr.beapp.interviews.bicloo.kmm.core.di.DI
import org.kodein.di.instance


enum class StrategyType {
	JUST_ASYNC,
	JUST_CACHE,
	CACHE_OR_ASYNC,
	CACHE_THEN_ASYNC,
	ASYNC_OR_CACHE,
	NO_CACHE;

	internal fun getStrategy(): CacheStrategy {
		return when (this) {
			JUST_ASYNC -> {
				val instance by DI.instance<JustAsyncStrategy>()
				instance
			}
			JUST_CACHE -> {
				val instance by DI.instance<JustCacheStrategy>()
				instance
			}
			CACHE_OR_ASYNC -> {
				val instance by DI.instance<CacheOrAsyncStrategy>()
				instance
			}
			CACHE_THEN_ASYNC -> {
				val instance by DI.instance<CacheThenAsyncStrategy>()
				instance
			}
			ASYNC_OR_CACHE -> {
				val instance by DI.instance<AsyncOrCacheStrategy>()
				instance
			}
			NO_CACHE -> {
				val instance by DI.instance<NoCacheStrategy>()
				instance
			}
		}
	}
}
