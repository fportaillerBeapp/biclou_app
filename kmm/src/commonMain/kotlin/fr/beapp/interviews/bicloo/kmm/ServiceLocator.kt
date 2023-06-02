package fr.beapp.interviews.bicloo.kmm

import fr.beapp.interviews.bicloo.kmm.core.di.DI
import fr.beapp.interviews.bicloo.kmm.presentation.contract.ContractPresenter
import fr.beapp.interviews.bicloo.kmm.presentation.preferences.PreferencesPresenter
import fr.beapp.interviews.bicloo.kmm.presentation.station.StationPresenter
import org.kodein.di.provider

object ServiceLocator {

	private val stationProvider by DI.provider<StationPresenter>()
	fun stationPresenter() = stationProvider.invoke()

	private val contractProvider by DI.provider<ContractPresenter>()
	fun contractPresenter() = contractProvider.invoke()

	private val preferencesProvider by DI.provider<PreferencesPresenter>()
	fun preferencesPresenter() = preferencesProvider.invoke()

}