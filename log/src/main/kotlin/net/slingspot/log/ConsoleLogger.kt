package net.slingspot.log

import net.slingspot.log.Logger.Level
import net.slingspot.log.Logger.Level.*

public class ConsoleLogger(override val logLevel: Level) : Logger {
    override fun v(tag: String, message: Message): Unit = log(VERBOSE, tag, message)

    override fun d(tag: String, message: Message): Unit = log(DEBUG, tag, message)

    override fun i(tag: String, message: Message): Unit = log(INFO, tag, message)

    override fun w(tag: String, throwable: Throwable?, message: Message): Unit = log(WARN, tag, message, throwable)

    override fun e(tag: String, throwable: Throwable?, message: Message): Unit = log(ERROR, tag, message, throwable)

    private fun log(level: Level, tag: String, message: Message, throwable: Throwable? = null) {
        val color = when (level) {
            ERROR -> errorColor
            WARN -> warnColor
            else -> reset
        }

        print(color + LogCommon.format(level, tag, message, throwable) + reset)
    }

    private companion object {
        private const val reset = "\u001B[0m"
        private const val red = "\u001B[31m"
        private const val yellow = "\u001B[33m"
        private const val warnColor = yellow
        private const val errorColor = red
    }
}
