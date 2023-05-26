package fr.beapp.interviews.bicloo.kmm.data

import kotlinx.serialization.Serializable

@Serializable
internal data class StatusDTO(
	val code: String?,
	val message: String?,
	val timestamp: String?,
)
