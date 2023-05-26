package fr.beapp.interviews.bicloo.kmm.core.rest.exception

import kotlinx.serialization.Serializable

@Serializable
data class KnownRestException(
    val knownError: KnownError,
    val serverMessage: String?
) : Exception(serverMessage) {

    companion object {
        fun fromCode(code: Int?, message: String?) = when (code) {
            400 -> KnownRestException(KnownError.BAD_REQUEST, message)
            401 -> KnownRestException(KnownError.UNAUTHORIZED, message)
            403 -> KnownRestException(KnownError.FORBIDDEN, message)
            404 -> KnownRestException(KnownError.NOT_FOUND, message)
            else -> KnownRestException(KnownError.UNKNOWN, message)
        }

    }

    enum class KnownError {
        BAD_REQUEST,
        FORBIDDEN,
        NOT_FOUND,
        UNAUTHORIZED,
        UNKNOWN;
    }
}
