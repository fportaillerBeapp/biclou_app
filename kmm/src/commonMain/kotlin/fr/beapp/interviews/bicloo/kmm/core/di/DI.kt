package fr.beapp.interviews.bicloo.kmm.core.di

import fr.beapp.interviews.bicloo.kmm.core.rest.RestClient
import fr.beapp.interviews.bicloo.kmm.core.rest.client.KHttpClient
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStorage
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.AsyncOrCacheStrategy
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.CacheOrAsyncStrategy
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.CacheThenAsyncStrategy
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.JustAsyncStrategy
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.JustCacheStrategy
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.NoCacheStrategy
import fr.beapp.interviews.bicloo.kmm.data.contract.ContractDataSource
import fr.beapp.interviews.bicloo.kmm.data.station.StationDataSource
import fr.beapp.interviews.bicloo.kmm.logic.contract.ContractManager
import fr.beapp.interviews.bicloo.kmm.logic.station.StationManager
import fr.beapp.interviews.bicloo.kmm.presentation.contract.ContractPresenter
import fr.beapp.interviews.bicloo.kmm.presentation.station.StationPresenter
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

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

	// Stations
	bind<StationDataSource>() with singleton { StationDataSource(instance()) }
	bind<StationManager>() with singleton { StationManager(instance(), instance()) }
	bind<StationPresenter>() with singleton { StationPresenter(instance()) }

	// Contracts
	bind<ContractDataSource>() with singleton { ContractDataSource(instance()) }
	bind<ContractManager>() with singleton { ContractManager(instance(), instance()) }
	bind<ContractPresenter>() with singleton { ContractPresenter(instance()) }

}

expect fun clientEngine(): HttpClientEngine
