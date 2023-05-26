package fr.beapp.interviews.bicloo.kmm.core.log


// Implements dedicated delegate on each platform if necessary
object SharedLogger : SharedLoggerDelegate {
	var delegate: SharedLoggerDelegate = DefaultLoggerDelegate()

	override fun trace(message: String) = delegate.trace(message)
	override fun debug(message: String) = delegate.debug(message)
	override fun info(message: String) = delegate.info(message)
	override fun warn(message: String, throwable: Throwable?) = delegate.warn(message, throwable)
	override fun error(message: String, throwable: Throwable?) = delegate.error(message, throwable)

	class DefaultLoggerDelegate : SharedLoggerDelegate {
		override fun trace(message: String) = println(message)
		override fun debug(message: String) = println(message)
		override fun info(message: String) = println(message)
		override fun warn(message: String, throwable: Throwable?) = println("$message: $throwable")
		override fun error(message: String, throwable: Throwable?) = println("$message: $throwable")
	}
}

interface SharedLoggerDelegate {
	fun trace(message: String)
	fun debug(message: String)
	fun info(message: String)
	fun warn(message: String, throwable: Throwable? = null)
	fun error(message: String, throwable: Throwable? = null)
}
