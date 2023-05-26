package fr.beapp.interviews.bicloo.kmm.core.rest.exception

import kotlinx.serialization.Serializable

@Serializable
data class UnKnownRestException(
	val type: UnKnownRestExceptionType,
	override val message: String?,
) : Exception(message)

enum class UnKnownRestExceptionType {
	NETWORK,
	SERVER,
	TIMEOUT
}