package fr.beapp.interviews.bicloo.kmm.logic.station.entity

import kotlinx.serialization.Serializable

@Serializable
data class PositionEntity(
	val latitude: Double,
	val longitude: Double
)