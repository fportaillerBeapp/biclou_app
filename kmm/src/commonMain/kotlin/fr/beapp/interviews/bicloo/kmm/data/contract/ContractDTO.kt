package fr.beapp.interviews.bicloo.kmm.data.contract

import fr.beapp.interviews.bicloo.kmm.data.DTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ContractDTO(
	val cities: List<String>? = null,
	@SerialName("commercial_name") val commercialName: String? = null,
	@SerialName("country_code") val countryCode: String? = null,
	val name: String? = null
) : DTO<ContractEntity> {

	override fun toEntity(): ContractEntity = if (name.isNullOrBlank()) {
		throw DataIntegrityException(this)
	} else {
		ContractEntity(
			name = name,
			commercialName = commercialName ?: "",
			countryCode = countryCode ?: "",
			cities = cities ?: emptyList()
		)
	}
}