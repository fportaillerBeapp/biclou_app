package fr.beapp.interviews.bicloo.kmm.data.station

import fr.beapp.interviews.bicloo.kmm.data.DTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.PositionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PositionDTO(
	@SerialName("lat") val latitude: Double? = null,
	@SerialName("lng") val longitude: Double? = null
) : DTO<PositionEntity> {
	override fun toEntity(): PositionEntity = if (latitude == null || longitude == null)
		throw DataIntegrityException(this) else PositionEntity(
		latitude = latitude,
		longitude = longitude
	)
}