package fr.beapp.interviews.bicloo.andro

import android.app.Application
import fr.beapp.interviews.bicloo.kmm.core.CoreHelper.initCore
import fr.beapp.interviews.bicloo.kmm.core.Environment
import fr.beapp.interviews.bicloo.kmm.core.EnvironmentInfo

class BiclooApplication : Application() {

	companion object {
		private const val PREPROD_KEY = "preprod"
		private const val PROD_KEY = "prod"
	}

	override fun onCreate() {
		super.onCreate()

		"android".initCore(
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
	}
}