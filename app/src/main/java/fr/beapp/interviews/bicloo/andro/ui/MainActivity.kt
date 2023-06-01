package fr.beapp.interviews.bicloo.andro.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import fr.beapp.interviews.bicloo.andro.databinding.MainActivityBinding
import fr.beapp.interviews.bicloo.andro.ui.search.state.SearchState
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
		}
	}

	private fun showStationDetails(stationEntity: StationEntity?) {
		hideKeyboard()
		if (stationEntity != null) binding.mainActivitySearchFragment.isVisible = false
	}

	private fun onSearchResult(searchState: SearchState) {
		if (searchState is SearchState.Result
			&& searchState.groups.any { it.key == SearchState.SearchType.QUERY && it.value.isNotEmpty() }
		) {
			binding.mainActivitySearchFragment.isVisible = true

		}
	}
}