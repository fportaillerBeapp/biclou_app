package fr.beapp.interviews.bicloo.kmm.core

// Need to be initialized in onCreate() Application class, using CoreHelper.initCore(environment)
object CoreHelper {
    lateinit var appId: String
    lateinit var deviceId: String
    lateinit var wsVersion: String
    lateinit var environmentInfo: EnvironmentInfo

    fun initCore(
        appId: String,
        deviceId: String,
        wsVersion: String,
        environmentInfo: EnvironmentInfo
    ) {
        CoreHelper.appId = appId
        CoreHelper.deviceId = deviceId
        CoreHelper.wsVersion = wsVersion
        CoreHelper.environmentInfo = environmentInfo
    }
}

data class EnvironmentInfo(
    val environment: Environment,
    val urlHost: String,
)

enum class Environment { DEV, PREPROD, PROD }

internal expect val CLIENT_PLATFORM: ClientPlatform
enum class ClientPlatform { IOS, ANDROID }
