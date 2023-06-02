package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import fr.beapp.interviews.bicloo.andro.databinding.HeaderSearchViewHolderBinding

class FavoriteHeaderViewHolder(
	private val binding: HeaderSearchViewHolderBinding
) : FavoriteViewHolder(binding) {

	override fun bind(item: FavoriteItem) {
		val title = (item as? FavoriteItem.Header)?.title ?: return
		binding.root.setText(title)
	}
}