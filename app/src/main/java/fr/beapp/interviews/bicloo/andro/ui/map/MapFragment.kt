package fr.beapp.interviews.bicloo.andro.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import fr.beapp.interviews.bicloo.andro.databinding.MapFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.utils.StationCluster
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapReadyCallback {

	private lateinit var map: GoogleMap
	private lateinit var cluster: StationCluster
	private val viewModel: MainViewModel by activityViewModels()

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): MapFragmentBinding {
		return MapFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		(childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment)?.getMapAsync(this)
	}

	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		cluster = StationCluster(requireContext(), map)
		map.setOnCameraIdleListener(cluster)
		map.setOnMarkerClickListener(cluster)
		lifecycleScope.launch {
			whenResumed {
				viewModel.stations.collect(::onStationsUpdate)
			}
		}
	}

	private fun onStationsUpdate(stations: List<StationEntity>) {
		cluster.clearStations()
		cluster.addStations(stations.filter { it.position != null })
	}

	override fun onDestroyView() {
		super.onDestroyView()
		cluster.clearStations()
		map.clear()
	}
}