package fr.beapp.interviews.bicloo.kmm.presentation.station

import fr.beapp.interviews.bicloo.kmm.logic.station.StationManager
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.flow.Flow

class StationPresenter internal constructor(
	private val stationManager: StationManager
) {

	fun getStationsOfContract(contractName: String): Flow<List<StationEntity>> = stationManager.getStationsOfContract(contractName)

	fun getStationDetails(stationNumber: Int, contractName: String): Flow<StationEntity?> = stationManager.getStationDetails(stationNumber, contractName)
	fun getAllStations(): Flow<List<StationEntity>> = stationManager.getAllStations()
}