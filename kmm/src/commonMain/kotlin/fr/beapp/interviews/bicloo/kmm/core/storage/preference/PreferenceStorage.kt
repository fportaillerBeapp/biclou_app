package fr.beapp.interviews.bicloo.kmm.core.storage.preference

import fr.beapp.interviews.bicloo.kmm.core.di.json
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer

// Class allowing to put or get data from SharedPreferences in Android ; NSUserDefaults in iOS
expect class PreferenceStorage() {
	companion object {
		fun putBoolean(key: String, value: Boolean)
		fun getBoolean(key: String, defaultValue: Boolean): Boolean
		fun putString(key: String, value: String?)
		fun getString(key: String): String?
		fun putFloat(key: String, value: Float?)
		fun getFloat(key: String): Float?
		fun putDouble(key: String, value: Double?)
		fun getDouble(key: String): Double?
	}
}

fun <T> PreferenceStorage.Companion.getSerializable(key: String, serializer: KSerializer<T>): T? = getString(key)?.let {
	try {
		json.decodeFromString(serializer, it)
	} catch (e: Exception) {
		SharedLogger.warn("Fail to parse (key: $key) from PreferenceStorage", e)
		null
	}
}

fun <T> PreferenceStorage.Companion.putSerializable(key: String, value: T?, serializer: KSerializer<T>) {
	putString(key, value?.let { json.encodeToString(serializer, it) })
}

fun <T> PreferenceStorage.Companion.getList(key: String, serializer: KSerializer<T>): List<T> = PreferenceStorage.getSerializable(key, ListSerializer(serializer)) ?: listOf()

fun <T> PreferenceStorage.Companion.putList(key: String, list: List<T>, serializer: KSerializer<T>) {
	PreferenceStorage.putSerializable(key, list, ListSerializer(serializer))
}
