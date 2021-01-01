package net.slingspot.log

import net.slingspot.io.DefaultFileSystem
import net.slingspot.io.FileSystem
import net.slingspot.io.TextFileRef
import net.slingspot.log.FileLogger.Companion.maxFileSizeBytes
import net.slingspot.log.FileLogger.Companion.maxLogDays
import net.slingspot.log.Logger.Level
import net.slingspot.log.Logger.Level.*
import net.slingspot.time.midnightNextDay
import net.slingspot.time.now
import net.slingspot.time.simpleDateFormat
import net.slingspot.time.toMillis
import java.time.LocalDateTime

/**
 * Writes logs to files.
 *
 * Creates directories as follows:
 *
 * - logs/2021-01-09/
 * - logs/2021-01-10/
 * - logs/2021-01-11/
 *
 * Creates files like these within each directory:
 *
 * - 000001.log
 * - 000002.log
 * - 000003.log
 *
 * New log files will be created under any of these conditions:
 *
 * - the current log file exceeds [maxFileSizeBytes]
 * - the server is restarted
 * - Midnight UTC (begins logging in a new directory)
 *
 * Only the most recent log directories will be kept, up to [maxLogDays].
 */
public class FileLogger(
    override val logLevel: Level,
    private val logs: String = "logs",
    private val fileSystem: FileSystem = DefaultFileSystem()
) : Logger {
    private var targetFile: TextFileRef? = null
    private var tomorrow: Long = 0L

    override fun v(tag: String, message: Message) {
        write(now(), VERBOSE, tag, message)
    }

    override fun d(tag: String, message: Message) {
        write(now(), DEBUG, tag, message)
    }

    override fun i(tag: String, message: Message) {
        write(now(), INFO, tag, message)
    }

    override fun w(tag: String, throwable: Throwable?, message: Message) {
        write(now(), WARN, tag, message, throwable)
    }

    override fun e(tag: String, throwable: Throwable?, message: Message) {
        write(now(), ERROR, tag, message, throwable)
    }

    internal fun write(time: LocalDateTime, level: Level, tag: String, message: Message, throwable: Throwable? = null) {
        if (time.toMillis() >= tomorrow) {
            // Force a new log directory when the day rolls over at midnight.
            targetFile = null
        }

        targetFile = nextLogFile(time)

        targetFile?.let {
            it.append(LogCommon.format(level, tag, message, throwable))
            if (it.length() > maxFileSizeBytes) {
                targetFile = null
            }
        }
    }

    internal fun nextLogFile(time: LocalDateTime): TextFileRef? {
        return targetFile ?: try {
            // Create new logs/[date] directory if needed.
            val path = logs + fileSystem.separator + time.simpleDateFormat()

            val directory = fileSystem.directoryAt(path)
            directory.create()

            tomorrow = time.midnightNextDay()

            // Purge any old logs.
            val logDirectoryContents = fileSystem.directoryAt(logs).contents()
            if (logDirectoryContents.size > maxLogDays) {
                logDirectoryContents.dropLast(maxLogDays).forEach { fileRef ->
                    try {
                        fileRef.asDirectory()?.contents()?.forEach { it.delete() }
                        fileRef.delete()
                    } catch (e: Exception) {
                        // Ignore failure to remove directory and/or file. Move on to the next one.
                    }
                }
            }

            // Create new log file.
            directory.contents().maxOrNull()
                .let { numericNameOf(it?.path) + 1 }.toString().padStart(6, '0')
                .let { fileSystem.textFileAt(path + fileSystem.separator + it + logExtension) }
        } catch (e: Exception) {
            println("Failed to generate log file due to $e")
            null
        }
    }

    private fun numericNameOf(path: String?): Int {
        return try {
            path?.substringAfterLast(fileSystem.separator)?.removeSuffix(logExtension)?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
    }

    internal companion object {
        internal const val logExtension = ".log"
        internal const val maxFileSizeBytes = 1024 * 1024
        internal const val maxLogDays = 2
    }
}
