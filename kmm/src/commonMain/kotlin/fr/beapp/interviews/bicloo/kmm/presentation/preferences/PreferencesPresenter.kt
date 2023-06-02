package fr.beapp.interviews.bicloo.kmm.presentation.preferences

import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.preferences.PreferencesManager
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PreferencesPresenter {

	fun loadPreferredStations(): Flow<List<StationEntity>> = flow {
		emit(PreferencesManager.preferredStations)
	}


	fun loadPreferredContract(): Flow<ContractEntity?> = flow {
		emit(PreferencesManager.preferredContract)
	}

	fun setPreferredContract(contract: ContractEntity?) {
		PreferencesManager.preferredContract = contract
	}

	fun setPreferredStations(stations: List<StationEntity>) {
		PreferencesManager.preferredStations = stations
	}

	fun setOnboardingDone() {
		PreferencesManager.isOnboardingDone = true
	}

	fun isOnboardingDone(): Boolean = PreferencesManager.isOnboardingDone
}