package fr.beapp.interviews.bicloo.kmm.data

import fr.beapp.interviews.bicloo.kmm.core.rest.exception.KnownRestException
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
internal data class ResultDTO<out T>(
	val status: StatusDTO,
	val response: T
) {
	fun getResponseData(): T {
		return when {
			(status.code?.toInt() == HttpStatusCode.OK.value) -> response
			else -> throw KnownRestException.fromCode(status.code?.toInt(), status.message)
		}
	}
}
