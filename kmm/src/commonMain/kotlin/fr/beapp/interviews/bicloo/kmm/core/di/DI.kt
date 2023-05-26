package fr.beapp.interviews.bicloo.kmm.core.di

import fr.beapp.interviews.bicloo.kmm.core.rest.RestClient
import fr.beapp.interviews.bicloo.kmm.core.rest.client.KHttpClient
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStorage
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.*
import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.serialization.json.Json
import org.kodein.di.*

val json = Json {
	isLenient = true
	ignoreUnknownKeys = true
	allowStructuredMapKeys = true
}

val DI = DI {
	// Data Serialization
	bind<Json>() with singleton { json }

	// Cache management
	bind<CacheStorage>() with singleton { CacheStorage() }
	bind<CacheManager>() with singleton { CacheManager(instance(), instance()) }
	bind<CacheThenAsyncStrategy>() with singleton { CacheThenAsyncStrategy(instance(), instance()) }
	bind<CacheOrAsyncStrategy>() with singleton { CacheOrAsyncStrategy(instance(), instance()) }
	bind<JustCacheStrategy>() with singleton { JustCacheStrategy(instance(), instance()) }
	bind<JustAsyncStrategy>() with singleton { JustAsyncStrategy(instance(), instance()) }
	bind<AsyncOrCacheStrategy>() with singleton { AsyncOrCacheStrategy(instance(), instance()) }
	bind<NoCacheStrategy>() with singleton { NoCacheStrategy(instance(), instance()) }

	// Rest
	bind<HttpClientEngine>() with singleton { clientEngine() }
	bind<HttpClient>() with singleton { KHttpClient.client(instance(), instance()) }
	bind<RestClient>() with singleton { RestClient(instance()) }

}

expect fun clientEngine(): HttpClientEngine
