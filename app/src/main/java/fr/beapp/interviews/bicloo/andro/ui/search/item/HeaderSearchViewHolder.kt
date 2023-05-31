package fr.beapp.interviews.bicloo.andro.ui.search.item

import fr.beapp.interviews.bicloo.andro.databinding.HeaderSearchViewHolderBinding

class HeaderSearchViewHolder(private val binding: HeaderSearchViewHolderBinding) : SearchViewHolder(binding) {

	override fun bind(item: SearchItem) {
		if (item is SearchItem.Header) {
			binding.root.setText(item.title)
		}
	}
}