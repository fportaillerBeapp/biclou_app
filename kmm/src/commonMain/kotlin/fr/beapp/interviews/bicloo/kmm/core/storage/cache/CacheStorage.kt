package fr.beapp.interviews.bicloo.kmm.core.storage.cache

// Class allowing to put or get data from cache storage
expect class CacheStorage() {
	suspend fun write(key: String, value: String)
	suspend fun read(key: String): String?
	suspend fun delete(key: String): Boolean
	suspend fun clear()
	suspend fun clear(prefix: String)
}
