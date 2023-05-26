package fr.beapp.interviews.bicloo.kmm.data.contract

import fr.beapp.interviews.bicloo.kmm.core.rest.RestClient
import kotlinx.serialization.builtins.ListSerializer

internal class ContractDataSource(private val restClient: RestClient) {

	private val basePath = "/contracts"

	suspend fun getContracts(): List<ContractDTO> {
		return restClient.get(
			path = basePath,
			serializer = ListSerializer(ContractDTO.serializer()),
			isAuthRequired = false,
		)
	}
}