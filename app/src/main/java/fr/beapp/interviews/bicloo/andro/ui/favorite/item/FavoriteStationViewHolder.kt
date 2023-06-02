package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import fr.beapp.interviews.bicloo.andro.databinding.FavoriteViewHolderBinding
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class FavoriteStationViewHolder(
	private val binding: FavoriteViewHolderBinding,
	private val onStationClicked: (StationEntity) -> Unit
) : FavoriteViewHolder(binding) {

	override fun bind(item: FavoriteItem) {
		val station = (item as? FavoriteItem.Station)?.station ?: return
		binding.stationName.text = station.name
		binding.stationAvailability.text
		binding.root.setOnClickListener { onStationClicked(station) }
	}
}