package fr.beapp.interviews.bicloo.kmm.logic.station.entity

import kotlinx.serialization.Serializable

@Serializable
class StationEntity(
	val address: String,
	val contractName: String,
	val lastUpdate: String,
	val mainStands: StandEntity,
	val name: String,
	val number: Int,
	val overflow: Boolean,
	val overflowStands: StandEntity?,
	val position: PositionEntity,
	val status: Status,
	val totalStands: StandEntity
) {
	enum class Status {
		OPEN,
		CLOSED,
		UNKNOWN
	}
}