package fr.beapp.interviews.bicloo.kmm.logic.preferences

import fr.beapp.interviews.bicloo.kmm.core.di.json
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.storage.preference.PreferenceStorage
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.serialization.builtins.ListSerializer

internal object PreferencesManager {

	private const val KEY_PREFERRED_STATIONS = "KEY_PREFERRED_STATIONS"
	private const val KEY_IS_ONBOARDING_DONE = "KEY_IS_ONBOARDING_DONE"
	private const val KEY_PREFERRED_CONTRACT = "KEY_PREFERRED_CONTRACT"


	var preferredStations: List<StationEntity>
		get() = PreferenceStorage.getString(KEY_PREFERRED_STATIONS)?.let {
			try {
				json.decodeFromString(ListSerializer(StationEntity.serializer()), it)
			} catch (e: Throwable) {
				SharedLogger.warn("Fail to retrieve preferred stations from preferences", e)
				null
			}
		} ?: emptyList()
		set(value) = PreferenceStorage.putString(KEY_PREFERRED_STATIONS, json.encodeToString(ListSerializer(StationEntity.serializer()), value))


	var isOnboardingDone: Boolean
		get() = PreferenceStorage.getBoolean(KEY_IS_ONBOARDING_DONE, false)
		set(value) = PreferenceStorage.putBoolean(KEY_IS_ONBOARDING_DONE, value)


	var preferredContract: ContractEntity?
		get() = PreferenceStorage.getString(KEY_PREFERRED_CONTRACT)?.let {
			try {
				json.decodeFromString(ContractEntity.serializer(), it)
			} catch (e: Throwable) {
				SharedLogger.warn("Fail to retrieve preferred contract from preferences", e)
				null
			}
		}
		set(value) {
			if (value == null) PreferenceStorage.putString(KEY_PREFERRED_CONTRACT, null)
			else PreferenceStorage.putString(KEY_PREFERRED_CONTRACT, json.encodeToString(ContractEntity.serializer(), value))
		}
}