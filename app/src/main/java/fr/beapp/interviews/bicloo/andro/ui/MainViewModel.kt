package fr.beapp.interviews.bicloo.andro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.kmm.ServiceLocator
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

	private val _searchResult: MutableStateFlow<Map<Int, List<StationEntity>>> = MutableStateFlow(emptyMap())
	val searchResult: StateFlow<Map<Int, List<StationEntity>>>
		get() = _searchResult.asStateFlow()

	private val _stationDetail: MutableStateFlow<StationEntity?> = MutableStateFlow(null)
	val stationDetail: StateFlow<StationEntity?>
		get() = _stationDetail.asStateFlow()

	private val _location: MutableStateFlow<LatLng?> = MutableStateFlow(null)

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
			val querySearch = if (query.length > 3) async { SearchType.QUERY to searchStationByQuery(query) } else null
			val locationSearch = if (currentLocation != null) async { SearchType.LOCATION to searchStationByLocation(currentLocation) } else null
			val recentSearch = async { SearchType.RECENT to searchStationByRecent(query) }

			listOfNotNull(locationSearch, recentSearch, querySearch).map { it.await() }
				.fold(emptyMap<Int, List<StationEntity>>()) { acc, (type, stations) ->
					acc.toMutableMap().apply {
						this[type.ordinal] = stations
					}
				}
				.also {
					_searchResult.emit(it)
				}
		}
	}


	private fun searchStationByLocation(latLng: LatLng): List<StationEntity> {
		val searchZone = LatLngBounds(
			LatLng(latLng.latitude - 0.01, latLng.longitude - 0.01),
			LatLng(latLng.latitude + 0.01, latLng.longitude + 0.01)
		)
		return stations.value.filter { stationEntity ->
			stationEntity.position?.toLatLong()?.let { searchZone.contains(it) } == true
		}
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

	enum class SearchType {
		LOCATION, QUERY, RECENT
	}
}