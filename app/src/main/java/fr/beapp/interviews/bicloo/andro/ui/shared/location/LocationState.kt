package fr.beapp.interviews.bicloo.andro.ui.shared.location

import com.google.android.gms.maps.model.LatLng

sealed interface LocationState {
	data class Granted(val location: LatLng?) : LocationState
	object Denied : LocationState
	object NotYetRequested : LocationState
	object Requesting : LocationState
}