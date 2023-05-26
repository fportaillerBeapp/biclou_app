package fr.beapp.interviews.bicloo.kmm.core.file

import java.io.File

actual typealias KFile = File
actual fun KFile.toByteArray(): ByteArray = readBytes()
