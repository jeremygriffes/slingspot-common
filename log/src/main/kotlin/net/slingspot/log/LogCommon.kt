package net.slingspot.log

import net.slingspot.log.Logger.Level.*
import java.time.LocalDateTime

internal object LogCommon {
    private const val tab = "\u0009"
    private const val nestedExceptionDepth = 3

    internal fun format(
        level: Logger.Level,
        tag: String,
        message: Message,
        throwable: Throwable? = null
    ): String {
        val builder = StringBuilder()

        var thr = throwable

        val timestamp = LocalDateTime.now().toString()

        val levelIndicator = when (level) {
            VERBOSE -> "V"
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
        }

        val text = "$levelIndicator:[$tag]$tab$timestamp$tab" + try {
            message.invoke()
        } catch (t: Throwable) {
            thr = thr ?: t
            "Failed to build log message"
        }

        builder.appendLine(text)

        thr?.log(builder, nestedExceptionDepth)

        return builder.toString()
    }

    private fun Throwable.log(builder: StringBuilder, depth: Int) {
        with(builder) {
            message?.let { appendLine(tab + it) }
            stackTrace?.forEach { appendLine(tab + it) }

            cause?.let {
                if (depth > 0) {
                    appendLine("Caused by:")
                    it.log(builder, depth - 1)
                } else {
                    appendLine("Deeper nested exceptions have been ignored...")
                }
            }
        }
    }
}
