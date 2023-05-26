package fr.beapp.interviews.bicloo.kmm.core.storage.cache

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

// Need to be initialized in onCreate() Application class, using CacheStorage.init(cacheDir)
actual class CacheStorage {

	companion object {
		private lateinit var storageDir: File
		fun init(cacheDir: File) {
			if (!cacheDir.exists() && !cacheDir.mkdirs()) {
				throw RuntimeException("Couldn't create folder for cache " + cacheDir.absolutePath)
			}
			storageDir = cacheDir
		}
	}

	actual suspend fun write(key: String, value: String) {
		try {
			File(storageDir, key).outputStream()
				.use { it.write(value.toByteArray(Charset.defaultCharset())) }
		} catch (e: IOException) {
			SharedLogger.error("Fail to write in storage", e)
		}
	}

	actual suspend fun read(key: String): String? {
		return try {
			File(storageDir, key).inputStream().use { it.readBytes().decodeToString() }
		} catch (e: IOException) {
			SharedLogger.error("Fail to read in storage", e)
			null
		}
	}

	actual suspend fun delete(key: String): Boolean {
		return File(storageDir, key).safeDelete()
	}

	actual suspend fun clear() {
		storageDir.listFiles()?.forEach { it.safeDelete() }
	}

	actual suspend fun clear(prefix: String) {
		storageDir.listFiles()?.filter { it.name.startsWith(prefix) }?.forEach { it.safeDelete() }
	}

	private fun File.safeDelete() = try {
		delete()
	} catch (ignore: Exception) {
		false
	}
}
