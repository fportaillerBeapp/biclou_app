package fr.beapp.interviews.bicloo.andro.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import fr.beapp.interviews.bicloo.andro.databinding.MainActivityBinding
import fr.beapp.interviews.bicloo.andro.ui.utils.hideKeyboard
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


	private lateinit var binding: MainActivityBinding
	private val viewModel: MainViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = MainActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)

		prepareSearch()

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
}