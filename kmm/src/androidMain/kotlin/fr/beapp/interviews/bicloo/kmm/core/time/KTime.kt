@file:JvmName("KTimeJvm")
package fr.beapp.interviews.bicloo.kmm.core.time

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import java.time.Instant
import java.time.LocalDate

actual typealias KLocalDate = LocalDate
actual typealias KInstant = Instant

actual fun KLocalDate.toLocalDate() = kotlinx.datetime.LocalDate(year, monthValue, dayOfMonth)
actual fun kotlinx.datetime.LocalDate.toKLocalDate(): KLocalDate = toJavaLocalDate()
actual fun KInstant.toInstant() = kotlinx.datetime.Instant.fromEpochMilliseconds(toEpochMilli())
actual fun kotlinx.datetime.Instant.toKInstant() = toJavaInstant()
