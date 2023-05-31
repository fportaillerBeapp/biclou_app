package fr.beapp.interviews.bicloo.andro.utils

import com.google.android.gms.maps.model.LatLng
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.PositionEntity

fun PositionEntity.toLatLong(): LatLng = LatLng(
	this.latitude,
	this.longitude,
)