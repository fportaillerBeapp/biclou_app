package fr.beapp.interviews.bicloo.andro.ui.utils

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class StationCluster(context: Context, map: GoogleMap) : ClusterManager<StationCluster.StationClusterItem>(context, map) {


	fun addStations(stations: List<StationEntity>): Boolean = addItems(stations.map(::StationClusterItem))

	fun clearStations() = clearItems()


	class StationClusterItem(
		val station: StationEntity,
	) : ClusterItem {
		override fun getPosition(): LatLng = station.position?.toLatLong() ?: throw IllegalStateException("Station position must not be null")

		override fun getTitle(): String = station.name

		override fun getSnippet(): String = station.address

	}
}