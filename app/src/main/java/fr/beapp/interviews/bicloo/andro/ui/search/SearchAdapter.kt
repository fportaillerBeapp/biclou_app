package fr.beapp.interviews.bicloo.andro.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.beapp.interviews.bicloo.andro.databinding.HeaderSearchViewHolderBinding
import fr.beapp.interviews.bicloo.andro.databinding.StationSearchViewHolderBinding
import fr.beapp.interviews.bicloo.andro.ui.search.item.HeaderSearchViewHolder
import fr.beapp.interviews.bicloo.andro.ui.search.item.SearchItem
import fr.beapp.interviews.bicloo.andro.ui.search.item.SearchViewHolder
import fr.beapp.interviews.bicloo.andro.ui.search.item.StationSearchViewHolder
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class SearchAdapter(
	private val onStationClicked: (StationEntity) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

	companion object {
		private const val TYPE_HEADER = 0
		private const val TYPE_STATION = 1
	}

	private val items = mutableListOf<SearchItem>()

	fun replaceAll(groups: Map<Int, List<StationEntity>>) {
		items.clear()
		groups.forEach { (header, stations) ->
			items.add(SearchItem.Header(header))
			items.addAll(stations.map { SearchItem.Station(it) })
		}
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder = when (viewType) {
		TYPE_HEADER -> HeaderSearchViewHolder(HeaderSearchViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
		TYPE_STATION -> StationSearchViewHolder(
			StationSearchViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
			onStationClicked
		)

		else -> throw IllegalArgumentException("Unknown viewType $viewType")
	}

	override fun getItemCount(): Int = items.size

	override fun onBindViewHolder(holder: SearchViewHolder, position: Int) = items.getOrNull(position)?.let(holder::bind) ?: Unit

	override fun getItemViewType(position: Int): Int = when (items.getOrNull(position)) {
		is SearchItem.Header -> TYPE_HEADER
		is SearchItem.Station -> TYPE_STATION
		else -> throw IllegalArgumentException("Unknown item type at position $position")
	}


}