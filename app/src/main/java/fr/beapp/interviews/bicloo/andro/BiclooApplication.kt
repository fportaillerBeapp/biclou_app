package fr.beapp.interviews.bicloo.andro

import android.app.Application
import android.util.Log
import fr.beapp.interviews.bicloo.kmm.core.CoreHelper.initCore
import fr.beapp.interviews.bicloo.kmm.core.Environment
import fr.beapp.interviews.bicloo.kmm.core.EnvironmentInfo
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLoggerDelegate
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheStorage
import fr.beapp.interviews.bicloo.kmm.core.storage.preference.PreferenceStorage

class BiclooApplication : Application() {

	companion object {
		private const val PREPROD_KEY = "preprod"
		private const val PROD_KEY = "prod"
	}

	override fun onCreate() {
		super.onCreate()

		SharedLogger.delegate = object : SharedLoggerDelegate {
			override fun trace(message: String) {
				Log.println(Log.VERBOSE, null, message)
			}

			override fun debug(message: String) {
				Log.println(Log.DEBUG, null, message)
			}

			override fun info(message: String) {
				Log.println(Log.INFO, null, message)
			}

			override fun warn(message: String, throwable: Throwable?) {
				Log.w(null, message, throwable)
			}

			override fun error(message: String, throwable: Throwable?) {
				Log.e(null, message, throwable)
			}
		}

		initCore(
			apiKey = BuildConfig.JCDECAUX_API_KEY,
			environmentInfo = EnvironmentInfo(
				environment = when (BuildConfig.FLAVOR) {
					PREPROD_KEY -> Environment.PREPROD
					PROD_KEY -> Environment.PROD
					else -> Environment.QA
				},
				urlHost = BuildConfig.JCDECAUX_API
			)
		)

		PreferenceStorage.init(this)
		CacheStorage.init(this.cacheDir)
	}
}