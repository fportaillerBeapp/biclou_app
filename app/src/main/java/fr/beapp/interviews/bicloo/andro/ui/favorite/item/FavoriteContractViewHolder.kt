package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import fr.beapp.interviews.bicloo.andro.databinding.FavoriteContractViewHolderBinding
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity

class FavoriteContractViewHolder(
	private val binding: FavoriteContractViewHolderBinding,
	private val onContractClicked: (ContractEntity) -> Unit
) : FavoriteViewHolder(binding) {


	override fun bind(item: FavoriteItem) {
		val contract = (item as? FavoriteItem.Contract)?.contract ?: return
		//TODO: Feature Favorite Contract
		//TODO: bind contract
	}
}