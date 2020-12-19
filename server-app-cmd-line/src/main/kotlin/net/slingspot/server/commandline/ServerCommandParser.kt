package net.slingspot.server.commandline

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import net.slingspot.log.ConsoleLogger
import net.slingspot.log.FileLogger
import net.slingspot.log.Log
import net.slingspot.log.Logger
import net.slingspot.net.validPortFrom
import net.slingspot.server.Config
import net.slingspot.server.Content
import net.slingspot.server.Environment

/**
 * Parses the command line arguments and starts the server. Initializes console and file logging.
 */
public fun parse(vararg args: String, content: Content?, start: (Int, Int, Config) -> Unit) {
    ServerCommandParser(content, start).main(args)
}

internal class ServerCommandParser(
    private val content: Content?,
    private val start: (Int, Int, Config) -> Unit
) : CliktCommand() {
    private val keystorePath by option(
        "-f",
        "--file",
        envvar = "KEYSTORE_PATH",
        help = "path to keystore file"
    ).required()

    private val keystoreType by option(
        "-t",
        "--type",
        envvar = "KEYSTORE_TYPE",
        help = "keystore type, for example PKCS12"
    ).default("PKCS12")

    private val keystorePassword by option(
        "-p",
        "--password",
        envvar = "KEYSTORE_PASSWORD",
        help = "keystore password"
    ).required()

    private val environment by option(
        "-e",
        "--environment",
        envvar = "ENVIRONMENT",
        help = "deployment option: ${Environment.Development}, ${Environment.Staging}, or ${Environment.Production}"
    ).default(Environment.Development.toString())

    private val http by option(
        "--http",
        help = "HTTP port"
    ).int().default(80)

    private val https by option(
        "--https",
        help = "HTTPS port"
    ).int().default(443)

    private val consoleLogLevel by option(
        "--console-log",
        envvar = "CONSOLE_LOG",
        help = "Console log level: ${Logger.Level.VERBOSE}, ${Logger.Level.DEBUG}, ${Logger.Level.INFO}, ${Logger.Level.WARN}, ${Logger.Level.ERROR}"
    )

    private val fileLogLevel by option(
        "--file-log",
        envvar = "FILE_LOG",
        help = "File log level: ${Logger.Level.VERBOSE}, ${Logger.Level.DEBUG}, ${Logger.Level.INFO}, ${Logger.Level.WARN}, ${Logger.Level.ERROR}"
    )

    private val logDirectory by option("-o", "--out", envvar = "FILE_LOG_DIR", help = "output directory for log files")

    private fun logLevelFrom(value: String?) = when (value) {
        "DEBUG", "debug" -> Logger.Level.DEBUG
        "INFO", "info" -> Logger.Level.INFO
        "WARN", "warn" -> Logger.Level.WARN
        "ERROR", "error" -> Logger.Level.ERROR
        else -> Logger.Level.VERBOSE
    }

    override fun run() {
        val consoleLogger = consoleLogLevel?.let {
            ConsoleLogger(logLevelFrom(it))
        }

        val fileLogger = logDirectory?.let {
            FileLogger(logLevelFrom(fileLogLevel), it)
        }

        Log.loggers = listOfNotNull(consoleLogger, fileLogger)

        validPortFrom(http) ?: throw IllegalArgumentException("Invalid HTTP port")
        validPortFrom(https) ?: throw IllegalArgumentException("Invalid HTTPS port")

        start(
            http,
            https,
            Config(
                Environment.from(environment),
                keystorePath,
                keystoreType,
                keystorePassword,
                content
            )
        )
    }
}
