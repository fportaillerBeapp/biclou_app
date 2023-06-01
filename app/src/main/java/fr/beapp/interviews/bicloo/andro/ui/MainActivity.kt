package fr.beapp.interviews.bicloo.andro.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import fr.beapp.interviews.bicloo.andro.databinding.MainActivityBinding
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermission
import fr.beapp.interviews.bicloo.andro.ui.utils.hideKeyboard
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

	private lateinit var binding: MainActivityBinding
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private val viewModel: MainViewModel by viewModels()

	private val locationCallback by lazy {
		object : LocationCallback() {
			override fun onLocationResult(location: LocationResult) {
				// Ensure last location wasn't more than 10 seconds ago (avoid curious bugs)
				val lastLocation = location.lastLocation ?: return
				viewModel.setLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
				binding.mainAcitvityTopSearchBar.topSearchBarEnableLocation.isVisible = false
			}

			override fun onLocationAvailability(availability: LocationAvailability) {
				if (!availability.isLocationAvailable) viewModel.setLocation(null)
			}
		}
	}

	private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
		if (isGranted) listenToLocation()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = MainActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)

		prepareSearch()
		listenToLocation()

		viewModel.loadAllContracts()
		viewModel.loadAllStations()
		lifecycleScope.launch {
			withCreated {
				launch {
					viewModel.stationDetail.collect(::showStationDetails)
				}
			}
		}
	}

	private fun prepareSearch() {
		with(binding.mainAcitvityTopSearchBar) {
			topSearchBarEnableLocation.isVisible = !hasForegroundLocationPermission()
			topSearchBarEnableLocation.setOnClickListener {
				permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
			}
			topSearchBarSearchBar.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
				if (hasFocus) {
					viewModel.closeStationDetail()
				}
			}
			topSearchBarSearchBar.editText?.doAfterTextChanged {
				val input = it?.toString() ?: return@doAfterTextChanged
				binding.mainActivitySearchFragment.isVisible = input.length > 3
				viewModel.searchForStations(input)
			}
		}

		viewModel.searchForStations("")
	}

	@SuppressLint("MissingPermission")
	private fun listenToLocation() {
		if (!hasForegroundLocationPermission()) return
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		fusedLocationClient.requestLocationUpdates(
			create().apply {
				priority = Priority.PRIORITY_HIGH_ACCURACY
				fastestInterval = 1000
				interval = 2000
			},
			locationCallback,
			Looper.getMainLooper()
		)
	}

	private fun showStationDetails(stationEntity: StationEntity?) {
		hideKeyboard()
		binding.mainAcitvityTopSearchBar.topSearchBarSearchBar.editText?.clearFocus()
		if (stationEntity != null) binding.mainActivitySearchFragment.isVisible = false
		binding.mainActivityStationDetailsContainer.isVisible = stationEntity != null
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
}