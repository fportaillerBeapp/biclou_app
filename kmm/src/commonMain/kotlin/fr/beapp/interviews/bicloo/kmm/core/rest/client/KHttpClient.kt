package fr.beapp.interviews.bicloo.kmm.core.rest.client

import fr.beapp.interviews.bicloo.kmm.core.rest.exception.UnKnownRestException
import fr.beapp.interviews.bicloo.kmm.core.rest.exception.UnKnownRestExceptionType
import fr.beapp.interviews.bicloo.kmm.core.CoreHelper
import fr.beapp.interviews.bicloo.kmm.core.Environment
import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.rest.exception.NetworkLostException
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KHttpClient {

	fun client(engine: HttpClientEngine, json: Json): HttpClient {
		return HttpClient(engine) {
			install(HttpTimeout) {
				requestTimeoutMillis = 30_000L
				connectTimeoutMillis = 30_000L
				socketTimeoutMillis = 30_000L
			}
			install(ContentNegotiation) {
				json(json)
			}
			install(Logging) {
				logger = HttpLogger.DEFAULT
				level = if (CoreHelper.environmentInfo.environment == Environment.PROD) LogLevel.INFO else LogLevel.ALL
			}
			expectSuccess = false

			HttpResponseValidator {
				validateResponse { response ->
					val statusCode = response.status.value
					if (statusCode > 200) {
						SharedLogger.warn("[HTTP] call from ${response.request.url.encodedPath} return code $statusCode")
					}
					when (statusCode) {
						in 300..399 -> throw RedirectResponseException(response, "ERROR $statusCode")
						in 400..499 -> return@validateResponse
						in 500..599 -> throw ServerResponseException(response, "ERROR $statusCode")
					}

					if (statusCode >= 600) {
						throw ResponseException(response, "ERROR $statusCode")
					}
				}
				handleResponseExceptionWithRequest { throwable, _ ->
					when (throwable) {
						is ServerResponseException -> throw UnKnownRestException(
							UnKnownRestExceptionType.SERVER,
							throwable.message
						)
						is SocketTimeoutException,
						is HttpRequestTimeoutException -> throw UnKnownRestException(
							UnKnownRestExceptionType.TIMEOUT,
							throwable.message
						)
						is NetworkLostException -> throw UnKnownRestException(
							UnKnownRestExceptionType.NETWORK,
							throwable.message
						)
						else -> {
							SharedLogger.warn("[HTTP] about to throw up")
							throw throwable
						}
					}
				}
			}
		}
	}
}
