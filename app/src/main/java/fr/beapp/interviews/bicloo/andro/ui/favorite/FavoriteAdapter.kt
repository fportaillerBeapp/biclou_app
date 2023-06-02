package fr.beapp.interviews.bicloo.andro.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteContractViewHolderBinding
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteViewHolderBinding
import fr.beapp.interviews.bicloo.andro.databinding.HeaderSearchViewHolderBinding
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteContractViewHolder
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteHeaderViewHolder
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteItem
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteStationViewHolder
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteViewHolder
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class FavoriteAdapter(
	private val onStationClicked: (StationEntity) -> Unit,
	private val onContractClicked: (ContractEntity) -> Unit
) : RecyclerView.Adapter<FavoriteViewHolder>() {


	companion object {
		private const val TYPE_STATION = 0
		private const val TYPE_CONTRACT = 1
		private const val TYPE_HEADER = 2
	}

	private val items = mutableListOf<FavoriteItem>()

	fun replaceAll(contract: ContractEntity?, stations: List<StationEntity>) {
		items.clear()
		if (contract != null) {
			items.add(FavoriteItem.Header(R.string.favorite_contracts))
			items.add(FavoriteItem.Contract(contract))
		}
		items.add(FavoriteItem.Header(R.string.favorite_stations))
		items.addAll(stations.map(FavoriteItem::Station))
		notifyDataSetChanged()
	}

	fun clear() {
		items.clear()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder = when (viewType) {
		TYPE_HEADER -> FavoriteHeaderViewHolder(HeaderSearchViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
		TYPE_CONTRACT -> FavoriteContractViewHolder(
			FavoriteContractViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
			onContractClicked
		)

		TYPE_STATION -> FavoriteStationViewHolder(
			FavoriteViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
			onStationClicked
		)

		else -> throw IllegalArgumentException("Unknown viewType $viewType")
	}


	override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) = items.getOrNull(position)?.let(holder::bind) ?: Unit

	override fun getItemCount(): Int = items.size

	override fun getItemViewType(position: Int): Int = when (items.getOrNull(position)) {
		is FavoriteItem.Header -> TYPE_HEADER
		is FavoriteItem.Contract -> TYPE_CONTRACT
		is FavoriteItem.Station -> TYPE_STATION
		else -> throw IllegalArgumentException("Unknown viewType $position")
	}


}