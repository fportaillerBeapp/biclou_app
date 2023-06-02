package fr.beapp.interviews.bicloo.andro.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.MainActivityBinding
import fr.beapp.interviews.bicloo.andro.ui.favorite.FavoriteFragment
import fr.beapp.interviews.bicloo.andro.ui.map.MapFragment
import fr.beapp.interviews.bicloo.andro.ui.search.state.SearchState
import fr.beapp.interviews.bicloo.andro.ui.shared.location.LocationState
import fr.beapp.interviews.bicloo.andro.ui.shared.preferences.PreferencesViewModel
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermission
import fr.beapp.interviews.bicloo.andro.ui.utils.hasForegroundLocationPermissionDenied
import fr.beapp.interviews.bicloo.andro.ui.utils.hideKeyboard
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

	private lateinit var binding: MainActivityBinding
	private val viewModel: MainViewModel by viewModels()
	private val preferencesViewModel: PreferencesViewModel by viewModels()

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private val locationCallback by lazy {
		object : LocationCallback() {
			override fun onLocationResult(location: LocationResult) {
				// Ensure last location wasn't more than 10 seconds ago (avoid curious bugs)
				val lastLocation = location.lastLocation ?: return
				viewModel.setLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
			}

			override fun onLocationAvailability(availability: LocationAvailability) {
				if (!availability.isLocationAvailable) viewModel.setLocation(null)
			}
		}
	}

	private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
		if (isGranted) {
			viewModel.setLocationPermissionGranted()
			listenToLocation()
		} else viewModel.setLocationPermissionDenied()

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = MainActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)

		when {
			hasForegroundLocationPermission() -> {
				viewModel.setLocationPermissionGranted()
				listenToLocation()
			}

			hasForegroundLocationPermissionDenied() -> viewModel.setLocationPermissionDenied()
			else -> Unit // nothing to do as we will ask for permission later
		}

		prepareNavigationElements()

		viewModel.loadAllContracts()
		viewModel.loadAllStations()
		lifecycleScope.launch {
			withCreated {
				launch {
					viewModel.stationDetail.collect(::showStationDetails)
				}
			}
			withCreated {
				launch {
					viewModel.searchResult.collect(::onSearchResult)
				}
			}
			withCreated {
				launch {
					viewModel.location.collect(::onLocationStateUpdate)
				}
			}
		}
	}

	private fun prepareNavigationElements() {

		binding.mainActivityFragmentContainer.apply {
			isUserInputEnabled = false
			adapter = PagerAdapter(this@MainActivity, listOf(MapFragment.newInstance(), FavoriteFragment.newInstance()))
		}

		binding.mainActivityBottomNav.apply {
			setOnItemSelectedListener {
				val position = when (it.itemId) {
					R.id.main_menu_map -> 0
					R.id.main_menu_my_account -> 1
					else -> return@setOnItemSelectedListener false
				}
				binding.mainActivityFragmentContainer.setCurrentItem(position, true)
				true
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun listenToLocation() {
		if (!hasForegroundLocationPermission()) return
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

	private fun showStationDetails(stationEntity: StationEntity?) {
		hideKeyboard()
		if (binding.mainActivityFragmentContainer.currentItem != 0) {
			binding.mainActivityFragmentContainer.setCurrentItem(0, true)
			binding.mainActivityBottomNav.menu.getItem(0).isChecked = true
		}

		if (stationEntity != null) binding.mainActivitySearchFragment.isVisible = false
	}

	private fun onSearchResult(searchState: SearchState) {
		if (searchState is SearchState.Result
			&& searchState.groups.any { it.key == SearchState.SearchType.QUERY && it.value.isNotEmpty() }
		) {
			binding.mainActivitySearchFragment.isVisible = true

		}
	}

	private fun onLocationStateUpdate(state: LocationState) {
		if (state is LocationState.Requesting) permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
	}

	override fun onDestroy() {
		fusedLocationClient.removeLocationUpdates(locationCallback)
		fusedLocationClient.flushLocations()
		super.onDestroy()
	}


	private class PagerAdapter(fragmentActivity: FragmentActivity, private val fragments: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
		override fun getItemCount(): Int = fragments.size
		override fun createFragment(position: Int): Fragment = fragments[position]
	}
}