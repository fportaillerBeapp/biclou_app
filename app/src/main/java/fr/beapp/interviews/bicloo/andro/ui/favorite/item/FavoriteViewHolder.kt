package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import androidx.recyclerview.widget.RecyclerView
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteViewHolderBinding
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class FavoriteViewHolder(
	private val binding: FavoriteViewHolderBinding,
	private val onStationClicked: (StationEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

	fun bind(station: StationEntity) {
		binding.stationName.text = station.name
		binding.stationAvailability.text
		binding.root.setOnClickListener { onStationClicked(station) }
	}
}
