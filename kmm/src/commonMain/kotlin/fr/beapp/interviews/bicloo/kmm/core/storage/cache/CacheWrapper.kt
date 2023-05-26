package fr.beapp.interviews.bicloo.kmm.core.storage.cache

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
internal data class CacheWrapper<out T>(
	val data: T,
	val cachedDate: Long = Clock.System.now().toEpochMilliseconds()
)
