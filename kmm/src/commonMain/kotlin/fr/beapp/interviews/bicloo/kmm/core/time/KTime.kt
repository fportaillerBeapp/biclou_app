package fr.beapp.interviews.bicloo.kmm.core.time

import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Turn kotlinx.datetime.LocalDate & -.Instant to java.time.LocalDate & -.Instant for Android ; NSDate for iOS
expect class KLocalDate
expect class KInstant

expect fun KLocalDate.toLocalDate(): LocalDate
expect fun LocalDate.toKLocalDate(): KLocalDate
expect fun KInstant.toInstant(): Instant
expect fun Instant.toKInstant(): KInstant

fun now() = Clock.System.now()
fun today() = Clock.System.todayAt(TimeZone.currentSystemDefault())
fun String.toKLocalDate() = LocalDate.parse(this).toKLocalDate()
fun String.toKInstant() = try {
	Instant.parse(this).toKInstant()
} catch (e: Exception) {
	if (length > 22) Instant.parse(substring(0, 22)).toKInstant() // Due to date wrong format returned by server
	else throw e
}
fun KInstant.toKLocalDate() = toInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date.toKLocalDate()

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = KLocalDate::class)
object KLocalDateSerializer : KSerializer<KLocalDate> {
	override fun serialize(encoder: Encoder, value: KLocalDate) = LocalDate.serializer().serialize(encoder, value.toLocalDate())
	override fun deserialize(decoder: Decoder) = LocalDate.serializer().deserialize(decoder).toKLocalDate()
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = KInstant::class)
object KInstantSerializer : KSerializer<KInstant> {
	override fun serialize(encoder: Encoder, value: KInstant) = Instant.serializer().serialize(encoder, value.toInstant())
	override fun deserialize(decoder: Decoder) = Instant.serializer().deserialize(decoder).toKInstant()
}
