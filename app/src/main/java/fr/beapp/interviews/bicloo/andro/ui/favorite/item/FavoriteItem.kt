package fr.beapp.interviews.bicloo.andro.ui.favorite.item

import androidx.annotation.StringRes
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

sealed interface FavoriteItem {
	data class Station(val station: StationEntity) : FavoriteItem
	data class Header(@StringRes val title: Int) : FavoriteItem
	data class Contract(val contract: ContractEntity) : FavoriteItem
}