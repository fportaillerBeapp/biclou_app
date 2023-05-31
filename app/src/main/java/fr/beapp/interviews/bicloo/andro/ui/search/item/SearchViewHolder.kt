package fr.beapp.interviews.bicloo.andro.ui.search.item

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class SearchViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {


	abstract fun bind(item: SearchItem)
}
