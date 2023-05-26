package fr.beapp.interviews.bicloo.kmm.core.rest.client

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import io.ktor.client.plugins.logging.*

class HttpLogger : Logger {

	companion object {
		val DEFAULT: HttpLogger get() = HttpLogger()
	}

	override fun log(message: String) {
		SharedLogger.info("[HTTP] $message")
	}
}
