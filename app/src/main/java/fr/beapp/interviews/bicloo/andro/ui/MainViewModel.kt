package fr.beapp.interviews.bicloo.andro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.beapp.interviews.bicloo.kmm.ServiceLocator
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

}