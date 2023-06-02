package fr.beapp.interviews.bicloo.andro.ui.utils

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import fr.beapp.interviews.bicloo.andro.R
import fr.beapp.interviews.bicloo.andro.utils.toLatLong
import fr.beapp.interviews.bicloo.kmm.logic.station.entity.StationEntity

class StationCluster(context: Context, map: GoogleMap) : ClusterManager<StationCluster.StationClusterItem>(context, map) {

	init {
		renderer = StationClusterRenderer(context, map, this)
	}


	fun addStations(stations: List<StationEntity>): Boolean = addItems(stations.map(::StationClusterItem))

	fun clearStations() = clearItems()


	class StationClusterItem(
		val station: StationEntity,
	) : ClusterItem {
		override fun getPosition(): LatLng = station.position?.toLatLong() ?: throw IllegalStateException("Station position must not be null")

		override fun getTitle(): String = station.name

		override fun getSnippet(): String = station.address

	}

	private class StationClusterRenderer(
		context: Context,
		map: GoogleMap,
		clusterManager: StationCluster
	) : DefaultClusterRenderer<StationClusterItem>(context, map, clusterManager) {

		private val stationIcon = BitmapUtils.getBitmapFromVectorDrawable(context, R.drawable.ic_station_marker)?.let(BitmapDescriptorFactory::fromBitmap)

		override fun onBeforeClusterItemRendered(item: StationClusterItem, markerOptions: MarkerOptions) {
			super.onBeforeClusterItemRendered(item, markerOptions)
			markerOptions.icon(stationIcon)
		}
	}
}