package fr.beapp.interviews.bicloo.kmm.core.rest

import fr.beapp.interviews.bicloo.kmm.core.CoreHelper
import fr.beapp.interviews.bicloo.kmm.core.di.json
import fr.beapp.interviews.bicloo.kmm.data.ResultDTO
import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement

internal class RestClient(val client: HttpClient) {

	val urlHost = CoreHelper.environmentInfo.urlHost
	private val appId = CoreHelper.appId
	private val deviceId = CoreHelper.deviceId

	suspend inline fun <reified T> getWithStatus(
		path: String,
		params: Map<String, Any?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
		isDeviceIdRequired: Boolean = false,
	): ResultDTO<T> {
		val response = client.get {
			headers(isDeviceIdRequired)
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
			}
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText())
	}

	suspend inline fun <reified T> get(
		path: String,
		params: Map<String, Any?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
		isDeviceIdRequired: Boolean = false,
	): T {
		return getWithStatus(path, params, isAuthRequired, serializer, isDeviceIdRequired).getResponseData()
	}

	suspend inline fun <reified T> get(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
		isDeviceIdRequired: Boolean = false,
	): T {
		return get(
			path, params, isAuthRequired,
			serializer = MapSerializer(String.serializer(), JsonElement.serializer()),
			isDeviceIdRequired = isDeviceIdRequired
		)[key]?.let { json.decodeFromJsonElement(serializer, it) } ?: throw DataIntegrityException("Missing key $key")
	}

	suspend inline fun <reified U> postWithStatus(
		path: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): ResultDTO<U> = postWithStatus<Any, U>(path, params, content = null, isAuthRequired = isAuthRequired, serializer = serializer, isDeviceIdRequired = isDeviceIdRequired)

	suspend inline fun <reified T, reified U> postWithStatus(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): ResultDTO<U> {
		val response = client.post {
			headers(isDeviceIdRequired)
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
			}
			setBody(content ?: EmptyContent)
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText())
	}

	suspend inline fun <reified U> post(
		path: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false,
	): U = post<Any, U>(path = path, params = params, content = null, isAuthRequired = isAuthRequired, serializer = serializer, isDeviceIdRequired = isDeviceIdRequired)

	suspend inline fun <reified T, reified U> post(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false,
	): U {
		return postWithStatus(path, params, content, isAuthRequired, serializer, isDeviceIdRequired).getResponseData()
	}

	suspend inline fun <reified U> post(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): U = post<Any, U>(path, key, params, content = null, isAuthRequired = isAuthRequired, serializer = serializer, isDeviceIdRequired = isDeviceIdRequired)

	suspend inline fun <reified T, reified U> post(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): U {
		return post(
			path, params, content, isAuthRequired,
			serializer = MapSerializer(String.serializer(), serializer),
			isDeviceIdRequired = isDeviceIdRequired
		)[key] ?: throw DataIntegrityException("Missing key $key")
	}

	suspend inline fun <reified T, reified U> put(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T? = null,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): U {
		val response = client.put {
			headers(isDeviceIdRequired)
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
			}
			setBody(content ?: EmptyContent)
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText()).getResponseData()
	}

	suspend inline fun <reified T, reified U> delete(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T? = null,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
		isDeviceIdRequired: Boolean = false
	): U {
		val response = client.delete {
			headers(isDeviceIdRequired)
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
			}
			setBody(content ?: EmptyContent)
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText()).getResponseData()
	}

	internal fun HttpRequestBuilder.headers(isDeviceIdRequired: Boolean) {
		header("appid", appId)
		if (isDeviceIdRequired) header("deviceid", deviceId)
		header(HttpHeaders.ContentType, ContentType.Application.Json)
	}

	@Suppress("UNUSED_PARAMETER")
	internal fun HttpRequestBuilder.authParameter(isAuthRequired: Boolean) = Unit

	internal fun HttpRequestBuilder.addParameter(key: String, value: Any?): Unit =
		value?.let {
			when (it) {
				is List<*> -> it.forEach { element ->
					url.parameters.append(key, element.toString())
				}

				else -> url.parameters.append(key, it.toString())
			}
		} ?: Unit
}
