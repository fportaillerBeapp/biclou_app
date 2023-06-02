package fr.beapp.interviews.bicloo.andro.ui.shared.preferences.state

import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

sealed class PreferencesState(val contract: ContractEntity?, val stations: List<StationEntity>) {
	object Empty : PreferencesState(null, emptyList())
	class Updated(contract: ContractEntity?, stations: List<StationEntity>) : PreferencesState(contract, stations)
}