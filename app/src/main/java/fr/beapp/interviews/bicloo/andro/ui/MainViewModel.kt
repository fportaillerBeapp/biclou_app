package fr.beapp.interviews.bicloo.andro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import fr.beapp.interviews.bicloo.andro.ui.search.state.SearchState
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.andro.utils.toRadialBounds
import fr.beapp.interviews.bicloo.kmm.ServiceLocator
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

	private val contractPresenter by lazy { ServiceLocator.contractPresenter() }
	private val stationPresenter by lazy { ServiceLocator.stationPresenter() }

	private val _stations: MutableStateFlow<List<StationEntity>> = MutableStateFlow(emptyList())
	val stations: StateFlow<List<StationEntity>>
		get() = _stations.asStateFlow()

	private val _contracts: MutableStateFlow<List<ContractEntity>> = MutableStateFlow(emptyList())
	val contracts: StateFlow<List<ContractEntity>>
		get() = _contracts.asStateFlow()

	private val _searchResult: MutableStateFlow<SearchState> = MutableStateFlow(SearchState.Empty)
	val searchResult: StateFlow<SearchState>
		get() = _searchResult.asStateFlow()

	private val _stationDetail: MutableStateFlow<StationEntity?> = MutableStateFlow(null)
	val stationDetail: StateFlow<StationEntity?>
		get() = _stationDetail.asStateFlow()

	private val _location: MutableStateFlow<LatLng?> = MutableStateFlow(null)
	val location: StateFlow<LatLng?>
		get() = _location.asStateFlow()

	private val _shouldRequestLocationPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val shouldRequestLocationPermission: StateFlow<Boolean>
		get() = _shouldRequestLocationPermission.asStateFlow()

	fun loadAllContracts() {
		viewModelScope.launch {
			contractPresenter.getContracts().collect(_contracts::emit)
		}
	}

	fun loadAllStations() {
		viewModelScope.launch {
			stationPresenter.getAllStations().collect {
				_stations.emit(it)
			}
		}
	}

	fun loadStationsOfContract(contract: ContractEntity) {
		viewModelScope.launch {
			stationPresenter.getStationsOfContract(contract.name).collect(_stations::emit)
		}
	}

	fun searchForStations(query: String) {

		viewModelScope.launch {
			val currentLocation = _location.value
			val querySearch = if (query.length > 2) async { SearchState.SearchType.QUERY to searchStationByQuery(query) } else null
			val locationSearch = if (currentLocation != null) async { SearchState.SearchType.LOCATION to searchStationByLocation(currentLocation) } else null
			val recentSearch = async { SearchState.SearchType.RECENT to searchStationByRecent(query) }

			val resultGroups = listOfNotNull(
				locationSearch,
				recentSearch,
				querySearch
			)
				.map { it.await() }
				.fold(emptyMap<SearchState.SearchType, List<StationEntity>>()) { acc, (type, stations) ->
					acc.toMutableMap().apply {
						this[type] = stations
					}
				}

			_searchResult.update { SearchState.Result(query, currentLocation, resultGroups) }
		}
	}


	private fun searchStationByLocation(latLng: LatLng): List<StationEntity> {
		val searchZone = latLng.toRadialBounds()
		return stations.value.filter { stationEntity ->
			stationEntity.position?.toLatLong()?.let { searchZone.contains(it) } == true
		}.take(3) //TODO extract magic number to constant and explicit it
	}

	private fun searchStationByQuery(query: String): List<StationEntity> {
		return stations.value.filter {
			it.name.contains(query, ignoreCase = true) ||
					it.address.contains(query, ignoreCase = true) ||
					it.contractName?.contains(query, ignoreCase = true) == true
		}
	}

	private fun searchStationByRecent(query: String): List<StationEntity> {
		//TODO implement recent search
		return emptyList()
	}

	fun onStationClicked(stationEntity: StationEntity) {
		_stationDetail.value = stationEntity
	}

	fun closeStationDetail() {
		_stationDetail.value = null
	}

	fun setLocation(latLng: LatLng?) {
		_location.value = latLng
	}

	fun requestLocationPermission() {
		_shouldRequestLocationPermission.value = true
	}
}