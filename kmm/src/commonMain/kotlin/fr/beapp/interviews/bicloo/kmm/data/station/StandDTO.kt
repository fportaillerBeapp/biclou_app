package fr.beapp.interviews.bicloo.kmm.data.station

import fr.beapp.interviews.bicloo.kmm.data.DTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StandEntity
import kotlinx.serialization.Serializable

@Serializable
internal data class StandDTO(
	val availabilities: AvailabilitiesDTO? = null,
	val capacity: Int? = null
) : DTO<StandEntity> {
	override fun toEntity(): StandEntity = if (availabilities == null || capacity == null)
		throw DataIntegrityException(this) else StandEntity(
		availabilities = availabilities.toEntity(),
		capacity = capacity
	)
}
