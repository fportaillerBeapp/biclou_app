package fr.beapp.interviews.bicloo.kmm.core

// Need to be initialized in onCreate() Application class, using CoreHelper.initCore(environment)
object CoreHelper {
	lateinit var environmentInfo: EnvironmentInfo
	lateinit var apiKey: String

	fun String.initCore(
		environmentInfo: EnvironmentInfo,
		apiKey: String
	) {
		CoreHelper.environmentInfo = environmentInfo
	}
}

data class EnvironmentInfo(
	val environment: Environment,
	val urlHost: String,
)

enum class Environment { QA, PREPROD, PROD }

internal expect val CLIENT_PLATFORM: ClientPlatform

enum class ClientPlatform { IOS, ANDROID }
