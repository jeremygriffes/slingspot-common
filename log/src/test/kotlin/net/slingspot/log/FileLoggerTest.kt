package net.slingspot.log

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class FileLoggerTest {
    private val logDirectory = "mock"
    private val time = LocalDateTime.of(2021, Month.JANUARY, 20, 12, 0)

    @Test
    fun `creates new directory and log file from cold start`() {
        val fileSystem = MockFileSystem()

        val fileLogger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val file = fileLogger.nextLogFile(time)

        assertNotNull(file)
        assertEquals("$logDirectory${fileSystem.separator}2021-01-20${fileSystem.separator}000001.log", file!!.path)
    }

    @Test
    fun `log write`() {
        val fileSystem = MockFileSystem()

        val logger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val logMessage = "log message"
        logger.write(time, Logger.Level.VERBOSE, "test", { logMessage })

        val expectedLogFilePath = "$logDirectory${fileSystem.separator}2021-01-20${fileSystem.separator}000001.log"
        val logFile = fileSystem.affectedPaths[expectedLogFilePath] as MockFileSystem.MockTextFileRef
        val logs = logFile.ledger.toString()

        assertTrue(logs.contains(logMessage))
    }

    @Test
    fun `log rollover at midnight`() {
        val fileSystem = MockFileSystem()
        val logger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val nextDay = LocalDateTime.of(2021, Month.JANUARY, 21, 0, 0)

        logger.write(time, Logger.Level.VERBOSE, "test", { "day 1" })
        logger.write(nextDay, Logger.Level.VERBOSE, "test", { "day 2" })

        val pathDay1 = "$logDirectory${fileSystem.separator}2021-01-20${fileSystem.separator}000001.log"
        val pathDay2 = "$logDirectory${fileSystem.separator}2021-01-21${fileSystem.separator}000001.log"
        val logFile1 = fileSystem.affectedPaths[pathDay1] as MockFileSystem.MockTextFileRef
        val logFile2 = fileSystem.affectedPaths[pathDay2] as MockFileSystem.MockTextFileRef

        assertEquals(1, logFile1.ledger.toString().trim().lines().size)
        assertEquals(1, logFile2.ledger.toString().trim().lines().size)
    }

    @Test
    fun `no log rollover before midnight`() {
        val fileSystem = MockFileSystem()
        val logger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val almostNextDay = LocalDateTime.of(2021, Month.JANUARY, 20, 23, 59)

        logger.write(time, Logger.Level.VERBOSE, "test", { "day 1" })
        logger.write(almostNextDay, Logger.Level.VERBOSE, "test", { "almost day 2" })

        val path = "$logDirectory${fileSystem.separator}2021-01-20${fileSystem.separator}000001.log"
        val logFile = fileSystem.affectedPaths[path] as MockFileSystem.MockTextFileRef

        assertEquals(2, logFile.ledger.toString().trim().lines().size)
        assertFalse(fileSystem.affectedPaths.keys.any { it.contains("000002.log") })
    }

    @Test
    fun `log rollover when file exceeds max size`() {
        val fileSystem = MockFileSystem()
        val logger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val chars256 = StringBuilder().apply { repeat(16) { append("0123456789ABCDEF") } }.toString()

        // This should more than overflow the file, because each log line contains more than just the message.
        repeat(FileLogger.maxFileSizeBytes / chars256.length) {
            logger.write(time, Logger.Level.VERBOSE, "test", { chars256 })
        }

        // Logging should have generated a new log file.
        assertTrue(fileSystem.affectedPaths.keys.any { it.contains("000002.log") })
    }

    @Test
    fun `expired log directories are deleted`() {
        val fileSystem = MockFileSystem().apply { directoryAt(logDirectory) }
        val logger = FileLogger(Logger.Level.VERBOSE, logDirectory, fileSystem)
        val expiredDays = 3

        var date = LocalDateTime.of(2021, Month.JANUARY, 30, 10, 0)

        repeat(FileLogger.maxLogDays + expiredDays) { count ->
            logger.write(date, Logger.Level.VERBOSE, "test", { "day $count" })
            date = date.plusDays(1)
        }

        val fileRefs = fileSystem.affectedPaths
            .filter { it.key != logDirectory && !it.key.endsWith(FileLogger.logExtension) }
            .values.sortedBy { it.path }

        assertTrue(fileRefs.take(expiredDays).all { it.deleted })
        assertTrue(fileRefs.takeLast(FileLogger.maxLogDays).none { it.deleted })
    }
}
