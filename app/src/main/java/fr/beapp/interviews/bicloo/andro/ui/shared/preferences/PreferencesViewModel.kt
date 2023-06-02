package fr.beapp.interviews.bicloo.andro.ui.shared.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.beapp.interviews.bicloo.andro.ui.shared.preferences.state.PreferencesState
import fr.beapp.interviews.bicloo.kmm.ServiceLocator
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import fr.beapp.interviews.bicloo.kmm.presentation.preferences.PreferencesPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreferencesViewModel : ViewModel() {

	private val preferencesPresenter: PreferencesPresenter by lazy { ServiceLocator.preferencesPresenter() }

	private val _preferences = MutableStateFlow<PreferencesState>(PreferencesState.Empty)
	val preferences: StateFlow<PreferencesState>
		get() = _preferences.asStateFlow()


	fun loadPreferences() {
		viewModelScope.launch {
			preferencesPresenter.loadPreferredStations().collect { stations ->
				_preferences.update {
					PreferencesState.Updated(
						contract = null, // TODO: FEATURE Favorite Contract
						stations
					)
				}
			}
		}
	}

	fun togglePreferredStation(station: StationEntity): Boolean {
		val stations = _preferences.value.stations
		val foundStation = stations.find { it.name == station.name && it.number == station.number }
		return if (foundStation == null) {
			preferencesPresenter.setPreferredStations(stations.plus(station))
			true
		} else {
			preferencesPresenter.setPreferredStations(stations.minus(foundStation))
			false
		}
	}

	fun isStationFavorite(station: StationEntity): Boolean = _preferences.value.stations.any { it.name == station.name && it.number == station.number }
}