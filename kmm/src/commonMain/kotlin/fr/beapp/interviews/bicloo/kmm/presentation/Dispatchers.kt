package fr.beapp.interviews.bicloo.kmm.presentation

import kotlin.coroutines.CoroutineContext

expect val defaultDispatcher: CoroutineContext

expect val uiDispatcher: CoroutineContext
