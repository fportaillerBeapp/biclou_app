package fr.beapp.interviews.bicloo.andro.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.search.SearchAdapter
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity
import kotlinx.coroutines.launch

class FavoriteFragment : BaseFragment<FavoriteFragmentBinding>() {

	private val viewModel: MainViewModel by activityViewModels()
	private val adapter by lazy { SearchAdapter(this::onStationClicked) }

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): FavoriteFragmentBinding {
		return FavoriteFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.searchFragmentRecyclerView.adapter = adapter
		// binding.searchFragmentTopSearchBar.topSearchBarSearchBar.editText?.setBackgroundResource(R.drawable.search_fragment_search_edit_background)

		viewLifecycleOwner.lifecycleScope.launch {
			withResumed {
				launch {
					//viewModel.searchResult.collect(::onSearchStateUpdate)
				}
			}
		}

		/*binding.searchFragmentTopSearchBar.topSearchBarSearchBar.editText?.doAfterTextChanged {
			val input = it?.toString() ?: return@doAfterTextChanged
			viewModel.searchForStations(input)
		}*/
	}

	private fun onStationClicked(stationEntity: StationEntity) = viewModel.onStationClicked(stationEntity)

}