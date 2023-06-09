package fr.beapp.interviews.bicloo.kmm.presentation

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val uiDispatcher: CoroutineContext
	get() = Dispatchers.Main

actual val defaultDispatcher: CoroutineContext
	get() = Dispatchers.IO
