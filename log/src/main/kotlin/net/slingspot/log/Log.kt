package net.slingspot.log

import net.slingspot.log.Log.loggers
import net.slingspot.log.Logger.Level
import net.slingspot.log.Logger.Level.*

/**
 * Container for all installed Loggers. Logging to this Log object will distribute the log message to each logger.
 *
 * Loggers are initialized here via the [loggers] list, which must be initialized before performing logging.
 */
public object Log : Logger {
    @Suppress("MemberVisibilityCanBePrivate")
    public lateinit var loggers: List<Logger>

    /**
     * logLevel is unused for this top-level object.
     */
    override val logLevel: Level = VERBOSE

    override fun v(tag: String, message: Message) {
        loggers.forEach { if (it.logLevel <= VERBOSE) it.v(tag, message) }
    }

    override fun d(tag: String, message: Message) {
        loggers.forEach { if (it.logLevel <= DEBUG) it.d(tag, message) }
    }

    override fun i(tag: String, message: Message) {
        loggers.forEach { if (it.logLevel <= INFO) it.i(tag, message) }
    }

    override fun w(tag: String, throwable: Throwable?, message: Message) {
        loggers.forEach { if (it.logLevel <= WARN) it.w(tag, throwable, message) }
    }

    override fun e(tag: String, throwable: Throwable?, message: Message) {
        loggers.forEach { if (it.logLevel <= ERROR) it.e(tag, throwable, message) }
    }
}
