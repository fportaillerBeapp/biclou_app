package fr.beapp.interviews.bicloo.andro.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.beapp.interviews.bicloo.andro.databinding.FavoriteViewHolderBinding
import fr.beapp.interviews.bicloo.andro.ui.favorite.item.FavoriteViewHolder
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class FavoriteAdapter(
	private val onStationClicked: (StationEntity) -> Unit
) : RecyclerView.Adapter<FavoriteViewHolder>() {

	private val items = mutableListOf<StationEntity>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder =
		FavoriteViewHolder(
			FavoriteViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
			onStationClicked
		)

	override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) = items.getOrNull(position)?.let(holder::bind) ?: Unit

	override fun getItemCount(): Int = items.size


}