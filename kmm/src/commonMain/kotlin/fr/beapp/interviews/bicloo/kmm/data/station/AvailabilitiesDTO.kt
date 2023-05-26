package fr.beapp.interviews.bicloo.kmm.data.station

import fr.beapp.interviews.bicloo.kmm.data.DTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.AvailabilitiesEntity
import kotlinx.serialization.Serializable

@Serializable
internal data class AvailabilitiesDTO(
	val bikes: Int? = null,
	val electricalBikes: Int? = null,
	val electricalInternalBatteryBikes: Int? = null,
	val electricalRemovableBatteryBikes: Int? = null,
	val mechanicalBikes: Int? = null,
	val stands: Int? = null
) : DTO<AvailabilitiesEntity> {
	override fun toEntity(): AvailabilitiesEntity = if (
		bikes == null
		|| electricalBikes == null
		|| electricalInternalBatteryBikes == null
		|| electricalRemovableBatteryBikes == null
		|| mechanicalBikes == null
		|| stands == null
	) throw DataIntegrityException(this)
	else AvailabilitiesEntity(
		bikes = bikes,
		electricalBikes = electricalBikes,
		electricalInternalBatteryBikes = electricalInternalBatteryBikes,
		electricalRemovableBatteryBikes = electricalRemovableBatteryBikes,
		mechanicalBikes = mechanicalBikes,
		stands = stands
	)
}