package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class FavoriteViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

	abstract fun bind(item: FavoriteItem)
}
