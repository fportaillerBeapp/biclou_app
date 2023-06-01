package fr.beapp.interviews.bicloo.andro.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import kotlin.math.sqrt


//TODO extract magic numbers to constants and explicit them
fun LatLng.toRadialBounds(radiusInMeters: Double = 2_000.0): LatLngBounds {
	val distanceFromCenterToCorner = radiusInMeters * sqrt(2.0)
	val southwestCorner = SphericalUtil.computeOffset(this, distanceFromCenterToCorner, 225.0)
	val northeastCorner = SphericalUtil.computeOffset(this, distanceFromCenterToCorner, 45.0)
	return LatLngBounds(southwestCorner, northeastCorner)
}