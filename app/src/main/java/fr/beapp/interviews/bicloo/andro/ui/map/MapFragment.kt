package fr.beapp.interviews.bicloo.andro.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import fr.beapp.interviews.bicloo.andro.databinding.MapFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapReadyCallback {

	private lateinit var map: GoogleMap
	private val viewModel: MainViewModel by activityViewModels()
	private val mapViewModel: MapViewModel by viewModels()

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): MapFragmentBinding {
		return MapFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		(childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment)?.getMapAsync(this)
	}

	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		lifecycleScope.launch {
			whenResumed {
				viewModel.stations.collect(::onStationsUpdate)
			}
		}
	}

	private fun onStationsUpdate(stations: List<StationEntity>) {
		val markers = stations.filter { it.position != null }.mapNotNull { station ->
			val marker = MarkerOptions().position(station.position!!.toLatLong())
				.title(station.name)
			map.addMarker(marker)
		}
		mapViewModel.saveMarkers(markers)
	}
}