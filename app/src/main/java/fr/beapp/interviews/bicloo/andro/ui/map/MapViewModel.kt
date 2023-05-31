package fr.beapp.interviews.bicloo.andro.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {

	private val _markers: MutableStateFlow<List<Marker>> = MutableStateFlow(emptyList())
	val markers: StateFlow<List<Marker>>
		get() = _markers.asStateFlow()


	fun saveMarkers(markers: List<Marker>) {
		_markers.value.forEach { it.remove() }
		_markers.update { markers }
	}
}