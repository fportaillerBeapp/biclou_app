package fr.beapp.interviews.bicloo.andro.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.MapFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.shared.location.LocationState
import fr.beapp.interviews.bicloo.andro.ui.shared.preferences.PreferencesViewModel
import fr.beapp.interviews.bicloo.andro.ui.utils.BitmapUtils
import fr.beapp.interviews.bicloo.andro.ui.utils.StationCluster
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermission
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapsSdkInitializedCallback, OnMapReadyCallback {

	companion object {
		fun newInstance() = MapFragment()
	}

	private lateinit var map: GoogleMap
	private lateinit var cluster: StationCluster
	private var userMarker: Marker? = null
	private val viewModel: MainViewModel by activityViewModels()
	private val preferencesViewModel: PreferencesViewModel by activityViewModels()

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
			withResumed {
				launch {
					viewModel.stations.collect(::onStationsUpdate)
				}
			}
		}
		lifecycleScope.launch {
			withResumed {
				launch {
					viewModel.location.collect(::onUserLocationUpdate)
				}
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
		preferencesViewModel.loadPreferences()
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
			if (userMarker == null) {
				userMarker = map.addMarker(
					MarkerOptions()
						.apply {
							val bitmap = BitmapUtils.getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_pin) ?: return@apply
							icon(BitmapDescriptorFactory.fromBitmap(bitmap))
						}
						.position(state.location)
				)
				val zoom = if (map.cameraPosition.zoom < 10F) 16F else map.cameraPosition.zoom
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(state.location, zoom))
			} else userMarker?.position = state.location
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
		with(binding.mapFragmentStationDetails.favoriteIcon) {
			val isFavorite = preferencesViewModel.isStationFavorite(stationEntity)
			binding.mapFragmentStationDetails.favoriteIcon.icon = if (isFavorite) ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_filled)
			else ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_outlined)

			setOnClickListener {
				val isFavorite = preferencesViewModel.togglePreferredStation(stationEntity)
				binding.mapFragmentStationDetails.favoriteIcon.icon = if (isFavorite) ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_filled)
				else ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_outlined)
				preferencesViewModel.loadPreferences()
			}
		}
		stationEntity.position?.toLatLong()?.let {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 16F))
		}
		//TODO clear itineraries
	}

	override fun onDestroyView() {
		super.onDestroyView()
		cluster.clearStations()
		map.clear()
	}
}