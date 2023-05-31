package fr.beapp.interviews.bicloo.kmm.data.station

import fr.beapp.interviews.bicloo.kmm.core.rest.RestClient
import kotlinx.serialization.builtins.ListSerializer

internal class StationDataSource(private val restClient: RestClient) {

	companion object {
		private const val KEY_CONTRACT_NAME = "contract"
	}

	private val basePath = "/stations"

	suspend fun getStations(): List<StationDTO> = restClient.get(
		path = basePath,
		serializer = ListSerializer(StationDTO.serializer()),
		isAuthRequired = false,
	)

	suspend fun getStationsOfContract(contractName: String): List<StationDTO> {
		return restClient.get(
			path = basePath,
			params = mapOf(KEY_CONTRACT_NAME to contractName),
			serializer = ListSerializer(StationDTO.serializer()),
			isAuthRequired = false,
		)
	}

	suspend fun getStationDetails(number: Int, contractName: String): StationDTO? {
		return restClient.get(
			path = "$basePath/$number",
			params = mapOf(KEY_CONTRACT_NAME to contractName),
			serializer = StationDTO.serializer(),
			isAuthRequired = false,
		)
	}
}