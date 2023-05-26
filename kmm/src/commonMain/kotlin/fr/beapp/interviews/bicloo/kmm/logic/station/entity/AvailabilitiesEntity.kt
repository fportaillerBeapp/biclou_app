package fr.beapp.interviews.bicloo.kmm.logic.station.entity

import kotlinx.serialization.Serializable

@Serializable
data class AvailabilitiesEntity(
	val bikes: Int,
	val stands: Int,
	val mechanicalBikes: Int,
	val electricalBikes: Int,
	val electricalInternalBatteryBikes: Int,
	val electricalRemovableBatteryBikes: Int
)
