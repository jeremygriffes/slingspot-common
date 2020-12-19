package net.slingspot.log

import net.slingspot.log.Logger.Level
import net.slingspot.log.Logger.Level.*

public object Log : Logger {
    /**
     * Application must initialize the loggers before performing any logging.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public lateinit var loggers: List<Logger>

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
