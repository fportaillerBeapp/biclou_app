package fr.beapp.interviews.bicloo.kmm.core.storage.cache

internal data class CacheAwareWrapper<out T>(
	val fromCache: Boolean,
	val data: T
)
