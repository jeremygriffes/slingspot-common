package net.slingspot.time

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Current instant in time, UTC
 */
public fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

/**
 * Convert any LocalDateTime to millis, UTC
 */
public fun LocalDateTime.toMillis(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()

/**
 * Next day midnight in millis, UTC
 */
public fun LocalDateTime.midnightNextDay(): Long = plusDays(1)
    .truncatedTo(ChronoUnit.DAYS)
    .toMillis()

/**
 * Converts a date to string in ISO 8601 format (YYYY-MM-DD)
 */
public fun LocalDateTime.simpleDateFormat(): String =
    "$year-${month.value.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
