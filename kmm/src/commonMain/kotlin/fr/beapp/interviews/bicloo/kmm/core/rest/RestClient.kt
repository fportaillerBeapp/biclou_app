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
import io.ktor.client.request.parameter
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

	suspend inline fun <reified T> getWithStatus(
		path: String,
		params: Map<String, Any?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
	): ResultDTO<T> {
		val response = client.get {
			headers()
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
				setApiKey()
			}
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText())
	}

	suspend inline fun <reified T> get(
		path: String,
		params: Map<String, Any?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
	): T {
		return getWithStatus(path, params, isAuthRequired, serializer).getResponseData()
	}

	suspend inline fun <reified T> get(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<T>,
	): T {
		return get(
			path, params, isAuthRequired,
			serializer = MapSerializer(String.serializer(), JsonElement.serializer())
		)[key]?.let { json.decodeFromJsonElement(serializer, it) } ?: throw DataIntegrityException("Missing key $key")
	}

	suspend inline fun <reified U> postWithStatus(
		path: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
	) = postWithStatus<Any, U>(path, params, content = null, isAuthRequired = isAuthRequired, serializer = serializer)

	suspend inline fun <reified T, reified U> postWithStatus(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>
	): ResultDTO<U> {
		val response = client.post {
			headers()
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
				setApiKey()
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
	): U = post<Any, U>(path = path, params = params, content = null, isAuthRequired = isAuthRequired, serializer = serializer)

	suspend inline fun <reified T, reified U> post(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>,
	): U {
		return postWithStatus(path, params, content, isAuthRequired, serializer).getResponseData()
	}

	suspend inline fun <reified U> post(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		isAuthRequired: Boolean,
		serializer: KSerializer<U>
	): U = post<Any, U>(path, key, params, content = null, isAuthRequired = isAuthRequired, serializer = serializer)

	suspend inline fun <reified T, reified U> post(
		path: String,
		key: String,
		params: Map<String, String?> = emptyMap(),
		content: T?,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>
	): U {
		return post(
			path, params, content, isAuthRequired, MapSerializer(String.serializer(), serializer)
		)[key] ?: throw DataIntegrityException("Missing key $key")
	}

	suspend inline fun <reified T, reified U> put(
		path: String,
		params: Map<String, String?> = emptyMap(),
		content: T? = null,
		isAuthRequired: Boolean,
		serializer: KSerializer<U>
	): U {
		val response = client.put {
			headers()
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
				setApiKey()
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
		serializer: KSerializer<U>
	): U {
		val response = client.delete {
			headers()
			url {
				protocol = URLProtocol.HTTPS
				host = urlHost
				encodedPath = path
				params.forEach { param -> param.value?.let { addParameter(param.key, it) } }
				authParameter(isAuthRequired)
				setApiKey()
			}
			setBody(content ?: EmptyContent)
		}
		return json.decodeFromString(ResultDTO.serializer(serializer), response.bodyAsText()).getResponseData()
	}

	internal fun HttpRequestBuilder.headers() {
		header(HttpHeaders.ContentType, ContentType.Application.Json)
	}

	@Suppress("UNUSED_PARAMETER")
	internal fun HttpRequestBuilder.authParameter(isAuthRequired: Boolean) = Unit

	internal fun HttpRequestBuilder.setApiKey() {
		parameter("api_key", CoreHelper.apiKey)
	}

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
