package fr.beapp.interviews.bicloo.andro.ui.search.item

import fr.beapp.interviews.bicloo.andro.databinding.StationSearchViewHolderBinding
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class StationSearchViewHolder(
	private val binding: StationSearchViewHolderBinding,
	private val onStationClicked: (StationEntity) -> Unit
) : SearchViewHolder(binding) {
	override fun bind(item: SearchItem) {
		val station = (item as? SearchItem.Station)?.stationEntity ?: return
		binding.root.text = station.name
		binding.root.setOnClickListener { onStationClicked(station) }
	}
}