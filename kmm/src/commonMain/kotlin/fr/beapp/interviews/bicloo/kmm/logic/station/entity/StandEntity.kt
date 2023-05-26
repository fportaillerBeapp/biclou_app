package fr.beapp.interviews.bicloo.kmm.logic.station.entity

import kotlinx.serialization.Serializable

@Serializable
data class StandEntity(
	val availabilities: AvailabilitiesEntity,
	val capacity: Int
)