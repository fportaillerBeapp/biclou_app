package fr.beapp.interviews.bicloo.kmm.core.di

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun clientEngine(): HttpClientEngine = OkHttp.create()
