package fr.beapp.interviews.bicloo.kmm.logic.station.entity

import kotlinx.serialization.Serializable

@Serializable
data class StationEntity(
	val address: String,
	val contractName: String?,
	val lastUpdate: String?,
	val mainStands: StandEntity?,
	val name: String,
	val number: Int,
	val overflow: OverflowEnum,
	val overflowStands: StandEntity?,
	val position: PositionEntity?,
	val status: StatusEnum,
	val totalStands: StandEntity?
) {
	enum class StatusEnum {
		OPEN,
		CLOSED,
		UNKNOWN
	}

	enum class OverflowEnum {
		NO_OVERFLOW,
		OVERFLOW,
		UNKNOWN;

		companion object {
			fun from(boolean: Boolean?): OverflowEnum = when (boolean) {
				true -> OVERFLOW
				false -> NO_OVERFLOW
				else -> UNKNOWN
			}
		}
	}
}