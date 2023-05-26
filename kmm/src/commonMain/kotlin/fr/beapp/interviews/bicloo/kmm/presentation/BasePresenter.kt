package fr.beapp.interviews.bicloo.kmm.presentation

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext


abstract class BasePresenter(
	coroutineContext: CoroutineContext
) {

	companion object {
		const val PAGINATION_FIRST_PAGE = 1
	}

	protected val scope = CoroutineScope(coroutineContext)

	/**
	 * Always call destroy() when presenter isn't used anymore,
	 * to ensure all flow to be completely released
	 */
	fun destroy() {
		scope.cancel()
	}

	suspend fun <T> invoke(
		query: Flow<T>,
		onLoading: () -> Unit,
		onSuccess: (T) -> Unit,
		onError: (Throwable) -> Unit
	) {
		query
			.onStart { onLoading() }
			.catch { error ->
				if (error !is CancellationException) {
					SharedLogger.warn(error.message ?: "Error without message")
					SharedLogger.error(error.stackTraceToString())
					onError.invoke(error)
				}
			}
			.onEach { onSuccess(it) }
			.flowOn(uiDispatcher)
			.collect()
	}
}
