package fr.beapp.interviews.bicloo.andro.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.withCreated
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.beapp.interviews.bicloo.andro.databinding.MapFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.utils.StationCluster
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermission
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MapFragment : BaseFragment<MapFragmentBinding>(), OnMapsSdkInitializedCallback, OnMapReadyCallback {

	private lateinit var map: GoogleMap
	private lateinit var cluster: StationCluster
	private var userMarker: Marker? = null
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private val viewModel: MainViewModel by activityViewModels()

	private val locationCallback by lazy {
		object : LocationCallback() {
			override fun onLocationResult(location: LocationResult) {
				// Ensure last location wasn't more than 10 seconds ago (avoid curious bugs)
				val lastLocation = location.lastLocation ?: return
				viewModel.setLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
				binding.mapFragmentTopSearchBar.topSearchBarEnableLocation.isVisible = false
			}

			override fun onLocationAvailability(availability: LocationAvailability) {
				if (!availability.isLocationAvailable) viewModel.setLocation(null)
			}
		}
	}

	private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
		if (isGranted) listenToLocation()
	}

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): MapFragmentBinding {
		return MapFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		MapsInitializer.initialize(requireActivity(), MapsInitializer.Renderer.LATEST, this)
		(childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment)?.getMapAsync(this)
		prepareSearch()
		listenToLocation()

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
		map.setOnMarkerClickListener(cluster)
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
				permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
			}
			topSearchBarSearchBar.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
				if (hasFocus) {
					viewModel.closeStationDetail()
				}else {
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


	@SuppressLint("MissingPermission")
	private fun listenToLocation() {
		if (!requireContext().hasForegroundLocationPermission()) return
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
		fusedLocationClient.requestLocationUpdates(
			LocationRequest.create().apply {
				priority = Priority.PRIORITY_HIGH_ACCURACY
				fastestInterval = 1000
				interval = 2000
			},
			locationCallback,
			Looper.getMainLooper()
		)
	}


	private fun onStationsUpdate(stations: List<StationEntity>) {
		cluster.clearStations()
		cluster.addStations(stations.filter { it.position != null })
	}

	private fun onUserLocationUpdate(location: LatLng?) {
		if (location == null) {
			userMarker?.remove()
			userMarker = null
		} else {
			map.addMarker(MarkerOptions().position(location))
			//TODO center and zoom in on the user
		}
	}

	private fun showStationDetails(stationEntity: StationEntity?) {
		binding.mapFragmentTopSearchBar.topSearchBarSearchBar.editText?.clearFocus()
		//TODO migrate it to the activity
		//if (stationEntity != null) binding.mainActivitySearchFragment.isVisible = false
		binding.mapFragmentStationDetailsContainer.isVisible = stationEntity != null
		if (stationEntity == null) return
		//TODO bind station Details view here (binding.mainActivityStationDetails....)
		//TODO center map on station location
		//TODO clear itineraries
	}

	override fun onDestroy() {
		fusedLocationClient.removeLocationUpdates(locationCallback)
		fusedLocationClient.flushLocations()
		super.onDestroy()
	}

	override fun onDestroyView() {
		fusedLocationClient.removeLocationUpdates(locationCallback)
		fusedLocationClient.flushLocations()
		super.onDestroyView()
		cluster.clearStations()
		map.clear()
	}
}