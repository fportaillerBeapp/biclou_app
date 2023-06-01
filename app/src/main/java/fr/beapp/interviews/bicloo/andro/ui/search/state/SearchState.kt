package fr.beapp.interviews.bicloo.andro.ui.search.state

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

sealed interface SearchState {
	data class Result(
		val query: String,
		val location: LatLng?,
		val groups: Map<SearchType, List<StationEntity>>,
	) : SearchState

	object Empty : SearchState

	enum class SearchType(@StringRes val title: Int) {
		LOCATION(R.string.searchFragment_location_label),
		QUERY(R.string.searchFragment_query_label),
		RECENT(R.string.searchFragment_recent_label)
	}
}
