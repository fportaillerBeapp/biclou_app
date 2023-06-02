package fr.beapp.interviews.bicloo.andro.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.SearchFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.search.state.SearchState
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.shared.location.LocationState
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
		binding.searchFragmentTopSearchBar.topSearchBarSearchBar.editText?.setBackgroundResource(R.drawable.search_fragment_search_edit_background)

		viewLifecycleOwner.lifecycleScope.launch {
			withResumed {
				launch {
					viewModel.searchResult.collect(::onSearchStateUpdate)
				}
			}
			withResumed {
				launch {
					viewModel.location.collect(::onLocationStateUpdate)
				}
			}
		}

		binding.searchFragmentTopSearchBar.topSearchBarSearchBar.editText?.doAfterTextChanged {
			val input = it?.toString() ?: return@doAfterTextChanged
			viewModel.searchForStations(input)
		}
	}

	private fun onSearchStateUpdate(searchState: SearchState) {
		when (searchState) {
			is SearchState.Result -> {
				with(binding.searchFragmentTopSearchBar.topSearchBarSearchBar.editText) {
					if (this?.text.toString() != searchState.query && searchState.query.length > 2) {
						this?.setText(searchState.query)
						this?.requestFocus()
					}
				}

				adapter.replaceAll(searchState.groups)
			}

			is SearchState.Empty -> adapter.replaceAll(emptyMap())
		}
	}

	private fun onLocationStateUpdate(state: LocationState) {
		binding.searchFragmentTopSearchBar.topSearchBarEnableLocation.isVisible = state !is LocationState.Denied && state !is LocationState.Granted
	}

	private fun onStationClicked(stationEntity: StationEntity) = viewModel.onStationClicked(stationEntity)

}