package fr.beapp.interviews.bicloo.andro.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.withCreated
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.MapFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.shared.location.LocationState
import fr.beapp.interviews.bicloo.andro.ui.utils.StationCluster
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermission
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapsSdkInitializedCallback, OnMapReadyCallback {

	private lateinit var map: GoogleMap
	private lateinit var cluster: StationCluster
	private var userMarker: Marker? = null
	private val viewModel: MainViewModel by activityViewModels()

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): MapFragmentBinding {
		return MapFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		MapsInitializer.initialize(requireActivity(), MapsInitializer.Renderer.LATEST, this)
		(childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment)?.getMapAsync(this)
		prepareSearch()

		lifecycleScope.launch {
			withCreated {
				launch {
					viewModel.stationDetail.collect(::showStationDetails)
				}
			}
		}
	}

	override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) = Unit

	@SuppressLint("PotentialBehaviorOverride")
	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		cluster = StationCluster(requireContext(), map)
		map.setOnCameraIdleListener(cluster)
		cluster.setOnClusterItemClickListener {
			val station = it.station
			viewModel.onStationClicked(station)
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 16F))
			true
		}
		lifecycleScope.launch {
			whenResumed {
				viewModel.stations.collect(::onStationsUpdate)
			}
		}
		lifecycleScope.launch {
			whenResumed {
				viewModel.location.collect(::onUserLocationUpdate)
			}
		}
	}

	private fun prepareSearch() {
		with(binding.mapFragmentTopSearchBar) {
			topSearchBarEnableLocation.isVisible = !requireContext().hasForegroundLocationPermission()
			topSearchBarEnableLocation.setOnClickListener {
				viewModel.requestLocationPermission()
			}
			topSearchBarSearchBar.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
				if (hasFocus) {
					viewModel.closeStationDetail()
				} else {
					topSearchBarSearchBar.editText?.setText("")
				}
			}
			topSearchBarSearchBar.editText?.doAfterTextChanged {
				val input = it?.toString() ?: return@doAfterTextChanged
				viewModel.searchForStations(input)
			}
		}

		viewModel.searchForStations("")
	}

	private fun onStationsUpdate(stations: List<StationEntity>) {
		cluster.clearStations()
		cluster.addStations(stations.filter { it.position != null })
	}

	private fun onUserLocationUpdate(state: LocationState) {
		if (state !is LocationState.Granted) return
		if (state.location == null) {
			userMarker?.remove()
			userMarker = null
		} else {
			binding.mapFragmentTopSearchBar.topSearchBarEnableLocation.isVisible = false
			map.addMarker(MarkerOptions().position(state.location))
			//TODO center and zoom in on the user
		}
	}

	private fun showStationDetails(stationEntity: StationEntity?) {
		binding.mapFragmentTopSearchBar.topSearchBarSearchBar.editText?.clearFocus()
		binding.mapFragmentStationDetailsContainer.isVisible = stationEntity != null
		if (stationEntity == null) return
		binding.mapFragmentStationDetails.stationName.text = stationEntity.name
		binding.mapFragmentStationDetails.stationAddress.text = stationEntity.address
		binding.mapFragmentStationDetails.stationAvailability.text =
			when (stationEntity.status) {
				StationEntity.StatusEnum.OPEN -> getString(R.string.station_opened)
				StationEntity.StatusEnum.CLOSED -> getString(R.string.station_closed)
				else -> getString(R.string.station_status_unknown)
			}
		binding.mapFragmentStationDetails.favoriteIcon.setOnClickListener {
			// TODO
			// Add into favorites station
			//binding.mainActivityStationDetails.favoriteIcon.icon = if(isFavorite) getDrawable(R.drawable.ic_favorite_filled)  else getDrawable(R.drawable.ic_favorite_outlined)
		}
		//TODO center map on station location
		//TODO clear itineraries
	}

	override fun onDestroyView() {
		super.onDestroyView()
		cluster.clearStations()
		map.clear()
	}
}