package fr.beapp.interviews.bicloo.andro.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteFragmentBinding
import fr.beapp.interviews.bicloo.andro.ui.MainViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.BaseFragment
import fr.beapp.interviews.bicloo.andro.ui.shared.preferences.PreferencesViewModel
import fr.beapp.interviews.bicloo.andro.ui.shared.preferences.state.PreferencesState
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import kotlinx.coroutines.launch

class FavoriteFragment : BaseFragment<FavoriteFragmentBinding>() {

	companion object {
		fun newInstance() = FavoriteFragment()
	}

	private val viewModel: MainViewModel by activityViewModels()
	private val preferencesViewModel: PreferencesViewModel by activityViewModels()
	private val adapter by lazy { FavoriteAdapter(viewModel::onStationClicked, ::onContractClicked) }

	override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): FavoriteFragmentBinding {
		return FavoriteFragmentBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.searchFragmentRecyclerView.adapter = adapter
		binding.favoriteFragmentClearAll.isVisible = false
		binding.favoriteFragmentClearAll.setOnClickListener { preferencesViewModel.clearFavoriteStations() }

		viewLifecycleOwner.lifecycleScope.launch {
			withResumed {
				launch {
					preferencesViewModel.preferences.collect(::onPreferencesUpdate)
				}
			}
		}
	}

	private fun onPreferencesUpdate(state: PreferencesState) {
		when (state) {
			is PreferencesState.Empty -> {
				binding.favoriteFragmentClearAll.isVisible = false
				adapter.clear()
			}
			is PreferencesState.Updated -> {
				binding.favoriteFragmentClearAll.isVisible = true
				adapter.replaceAll(state.contract, state.stations)
			}
		}
	}

	private fun onContractClicked(contract: ContractEntity) {
		//TODO: Feature Favorite Contract
	}

}