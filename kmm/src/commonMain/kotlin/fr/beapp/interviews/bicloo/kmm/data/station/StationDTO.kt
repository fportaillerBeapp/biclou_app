package fr.beapp.interviews.bicloo.kmm.data.station

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.data.DTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.serialization.Serializable

@Serializable
internal data class StationDTO(
	val address: String? = null,
	val contractName: String? = null,
	val lastUpdate: String? = null,
	val mainStands: StandDTO? = null,
	val name: String? = null,
	val number: Int? = null,
	val overflow: Boolean? = null,
	val overflowStands: StandDTO? = null,
	val position: PositionDTO? = null,
	val status: String? = null,
	val totalStands: StandDTO? = null
) : DTO<StationEntity> {
	override fun toEntity(): StationEntity = if (
		address.isNullOrBlank() ||
		position == null ||
		contractName.isNullOrBlank() ||
		lastUpdate.isNullOrBlank() ||
		mainStands == null ||
		name.isNullOrBlank() ||
		number == null ||
		overflow == null ||
		status.isNullOrBlank() ||
		totalStands == null
	) throw DataIntegrityException(this)
	else StationEntity(
		address = address,
		contractName = contractName,
		lastUpdate = lastUpdate,
		mainStands = mainStands.toEntity(),
		name = name,
		number = number,
		overflow = overflow,
		overflowStands = overflowStands?.toEntity(),
		position = position.toEntity(),
		status = try {
			StationEntity.Status.valueOf(status.uppercase())
		} catch (throwable: Throwable) {
			SharedLogger.warn("Unknown station status: $status", throwable)
			StationEntity.Status.UNKNOWN
		},
		totalStands = totalStands.toEntity()
	)
}