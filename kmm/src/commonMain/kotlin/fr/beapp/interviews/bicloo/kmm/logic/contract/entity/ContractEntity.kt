package fr.beapp.interviews.bicloo.kmm.logic.contract.entity

import kotlinx.serialization.Serializable

@Serializable
data class ContractEntity(
	val name: String,
	val commercialName: String,
	val countryCode: String,
	val cities: List<String>,
)