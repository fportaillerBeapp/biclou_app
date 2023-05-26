package fr.beapp.interviews.bicloo.kmm.core.storage.preference

import android.content.Context
import android.content.SharedPreferences
import fr.beapp.interviews.bicloo.kmm.core.CoreHelper

// Need to be initialized in onCreate() Application class, using PreferenceStorage.init(context)
actual class PreferenceStorage {
	actual companion object {
		private val SHARED_PREFERENCES_NAME = "${CoreHelper.appId}_preferences"
		private lateinit var sharedPreferences: SharedPreferences
		fun init(context: Context) {
			sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
		}

		actual fun putString(key: String, value: String?) {
			sharedPreferences.edit()
				.putString(key, value)
				.apply()
		}

		actual fun getString(key: String): String? {
			return sharedPreferences.getString(key, null)
		}

		actual fun putBoolean(key: String, value: Boolean) {
			sharedPreferences.edit()
				.putBoolean(key, value)
				.apply()
		}

		actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
			return sharedPreferences.getBoolean(key, defaultValue)
		}

		actual fun putFloat(key: String, value: Float?) {
			if (value == null) sharedPreferences.edit().remove(key).apply()
			else sharedPreferences.edit()
				.putFloat(key, value)
				.apply()
		}

		actual fun getFloat(key: String): Float? {
			return if (sharedPreferences.contains(key)) sharedPreferences.getFloat(key, 0f)
			else null
		}

		actual fun putDouble(key: String, value: Double?) {
			if (value == null) sharedPreferences.edit().remove(key).apply()
			else sharedPreferences.edit()
				.putLong(key, java.lang.Double.doubleToRawLongBits(value))
				.apply()
		}

		actual fun getDouble(key: String): Double? {
			return if (sharedPreferences.contains(key)) java.lang.Double.longBitsToDouble(sharedPreferences.getLong(key, 0))
			else null
		}
	}
}
