package net.slingspot.log

import net.slingspot.log.FileLogger.Companion.maxFileSizeBytes
import net.slingspot.log.FileLogger.Companion.maxLogDays
import net.slingspot.log.Logger.Level
import net.slingspot.log.Logger.Level.*
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit.DAYS

/**
 * Writes logs to files.
 *
 * Creates directories like this:
 *
 * logs/2021-01-09/
 * logs/2021-01-10/
 * logs/2021-01-11/
 *
 * Within each directory, creates files like these:
 *
 * 000001.log
 * 000002.log
 * 000003.log
 * ...
 *
 * When the current log file exceeds [maxFileSizeBytes], or if the server is restarted, a new log file will be created.
 *
 * Only the most recent log directories will be kept, up to [maxLogDays].
 */
public class FileLogger(override val logLevel: Level, private val logDirectory: String = "logs") : Logger {
    private var targetFile: File? = null
    private var tomorrow: Long = 0L

    override fun v(tag: String, message: Message) {
        write(VERBOSE, tag, message)
    }

    override fun d(tag: String, message: Message) {
        write(DEBUG, tag, message)
    }

    override fun i(tag: String, message: Message) {
        write(INFO, tag, message)
    }

    override fun w(tag: String, throwable: Throwable?, message: Message) {
        write(WARN, tag, message, throwable)
    }

    override fun e(tag: String, throwable: Throwable?, message: Message) {
        write(ERROR, tag, message, throwable)
    }

    private fun write(level: Level, tag: String, message: Message, throwable: Throwable? = null) {
        if (System.currentTimeMillis() >= tomorrow) {
            // Force a new log directory when the day rolls over at midnight.
            targetFile = null
        }

        targetFile = nextLogFile()

        targetFile?.let {
            it.appendText(LogCommon.format(level, tag, message, throwable))
            if (it.length() > maxFileSizeBytes) {
                targetFile = null
            }
        }
    }

    /**
     * Produces a string like "2021-01-31"
     */
    private fun LocalDateTime.simpleDateFormat() =
        "$year-${month.value.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"

    private fun nextLogFile(): File? {
        return targetFile ?: try {
            // Create new logs/[date] directory if needed.
            val now = LocalDateTime.now()
            val path = logDirectory + File.separator + now.simpleDateFormat()

            val directory = File(path)
            try {
                directory.mkdirs()
            } catch (e: Exception) {
                println("Failed to create logging directory")
                return null
            }

            tomorrow = now.plusDays(1).truncatedTo(DAYS).toInstant(ZoneOffset.UTC).toEpochMilli()

            // Purge any old logs.
            File(logDirectory).listFiles()?.sorted()?.let { logDirectoryContents ->
                if (logDirectoryContents.size > maxLogDays) {
                    logDirectoryContents.dropLast(maxLogDays).forEach { directory ->
                        try {
                            directory.listFiles()?.forEach { file ->
                                file.delete()
                            }
                            directory.delete()
                        } catch (e: Exception) {
                            // Ignore failure to remove directory and/or file. Move on to the next one.
                        }
                    }
                }
            }

            // Create new log file.
            directory.listFiles()?.maxOrNull()
                .let { (it?.nameWithoutExtension ?: "0").toInt() + 1 }.toString().padStart(6, '0')
                .let { File(path + File.separator + it + logExtension) }
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        private const val logExtension = ".log"
        private const val maxFileSizeBytes = 1024 * 1024
        private const val maxLogDays = 2
        private const val fileLog = "file"
    }
}
