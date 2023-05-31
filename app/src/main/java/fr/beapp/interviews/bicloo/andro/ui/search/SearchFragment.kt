package fr.beapp.interviews.bicloo.andro.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.SearchFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<SearchFragmentBinding>() {

	private val viewModel: MainViewModel by activityViewModels()
	private val adapter by lazy { SearchAdapter(this::onStationClicked) }

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): SearchFragmentBinding {
		return SearchFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.searchFragmentRecyclerView.adapter = adapter

		viewLifecycleOwner.lifecycleScope.launch {
			withResumed {
				launch {
					viewModel.searchResult.collect {
						val groups = it.mapKeys { (searchType, _) ->
							when (searchType) {
								MainViewModel.SearchType.LOCATION.ordinal -> R.string.searchFragment_location_label
								MainViewModel.SearchType.RECENT.ordinal -> R.string.searchFragment_recent_label
								else -> R.string.searchFragment_query_label
							}
						}
						adapter.replaceAll(groups)
					}
				}
			}
		}
	}

	private fun onStationClicked(stationEntity: StationEntity) = viewModel.onStationClicked(stationEntity)

}