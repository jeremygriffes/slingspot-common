package net.slingspot.log

internal typealias Message = () -> Any?

/**
 * General logging interface. Instances of Logger will be held statically by Log.kt; avoid holding resources in your
 * implementation.
 */
public interface Logger {
    public val logLevel: Level

    public fun v(tag: String, message: Message)
    public fun d(tag: String, message: Message)
    public fun i(tag: String, message: Message)
    public fun w(tag: String, throwable: Throwable? = null, message: Message)
    public fun e(tag: String, throwable: Throwable? = null, message: Message)

    public enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }
}
