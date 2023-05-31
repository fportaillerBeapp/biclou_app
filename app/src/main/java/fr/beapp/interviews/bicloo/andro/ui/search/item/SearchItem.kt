package fr.beapp.interviews.bicloo.andro.ui.search.item

import androidx.annotation.StringRes
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

sealed interface SearchItem {
	class Header(@StringRes val title: Int) : SearchItem

	class Station(val stationEntity: StationEntity) : SearchItem
}