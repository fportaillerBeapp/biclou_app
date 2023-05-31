package fr.beapp.interviews.bicloo.kmm.logic.station

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.StrategyType
import fr.beapp.interviews.bicloo.kmm.data.station.StationDataSource
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.builtins.ListSerializer

internal class StationManager(
	private val cacheManager: CacheManager,
	private val stationDataSource: StationDataSource
) {

	companion object {
		private const val KEY_STATIONS_OF = "StationManager.KEY_STATIONS_OF_"
		private const val KEY_ALL_STATIONS = "StationManager.KEY_ALL_STATIONS"
	}

	fun getAllStations(): Flow<List<StationEntity>> = cacheManager
		.from<List<StationEntity>>(KEY_ALL_STATIONS)
		.withAsync {
			stationDataSource.getStations().mapNotNull { stationDTO ->
				try {
					stationDTO.toEntity()
				} catch (throwable: Throwable) {
					SharedLogger.warn("Fail to parse station from all stations (skipped)", throwable)
					null
				}
			}
		}
		.withStrategy(StrategyType.ASYNC_OR_CACHE)
		.withTtl(CacheManager.StrategyBuilder.TTL_HOUR)
		.withSerializer(ListSerializer(StationEntity.serializer()))
		.execute()

	fun getStationsOfContract(contractName: String): Flow<List<StationEntity>> = cacheManager
		.from<List<StationEntity>>("$KEY_STATIONS_OF$contractName")
		.withAsync {
			stationDataSource.getStationsOfContract(contractName).mapNotNull { stationDTO ->
				try {
					stationDTO.toEntity(contractName)
				} catch (throwable: Throwable) {
					SharedLogger.warn("Fail to parse station from contract $contractName (skipped)", throwable)
					null
				}
			}
		}
		.withStrategy(StrategyType.ASYNC_OR_CACHE)
		.withSerializer(ListSerializer(StationEntity.serializer()))
		.withTtl(CacheManager.StrategyBuilder.TTL_HOUR)
		.execute()


	fun getStationDetails(number: Int, contractName: String): Flow<StationEntity?> = flow {
		val result = try {
			stationDataSource.getStationDetails(number, contractName)?.toEntity()
		} catch (throwable: Throwable) {
			SharedLogger.warn("Fail to parse station from number $number and contract $contractName (skipped)", throwable)
			null
		}
		emit(result)
	}
}