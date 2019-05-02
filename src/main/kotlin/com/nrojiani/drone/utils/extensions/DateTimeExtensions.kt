@file:JvmName("DateTimeExtensions")

package com.nrojiani.drone.utils.extensions

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import kotlin.math.abs

/*
 * This file contains extension functions for the Java 8 Date/Time API (LocalTime, LocalDate, Temporal, etc.).
 *
 * Note: in a "real-world" project, it would be preferable to keep extension functions
 * like this in a separate library.
 */

/**
 * Returns the [LocalDate] and [LocalTime] as a Pair
 *
 * Example:
 * ```
 * val (date, time) = myZonedDateTime.localDateAndTime
 * ```
 */
val ZonedDateTime.localDateAndTime: Pair<LocalDate, LocalTime> get() = toLocalDate() to toLocalTime()

/**
 * Returns the [LocalDate] and [OffsetTime] as a Pair
 *
 * Example:
 * ```
 * val (date, time) = myZonedDateTime.dateAndTime
 * ```
 */
val ZonedDateTime.dateAndTime: Pair<LocalDate, OffsetTime> get() = toLocalDate() to toLocalTime().atOffset(offset)

/**
 * Returns the [LocalDate] and [LocalTime] as a Pair
 *
 * Example:
 * ```
 * val (date, time) = myLocalDateTime.localDateAndTime
 * ```
 */
val LocalDateTime.dateAndTime: Pair<LocalDate, LocalTime> get() = toLocalDate() to toLocalTime()

/**
 * Returns the [LocalDate], [LocalTime], & [ZoneOffset] as a Triple
 *
 * Example:
 * ```
 * val (date, time) = myZonedDateTime.dateTimeOffset
 * ```
 */
val ZonedDateTime.dateTimeOffset: Triple<LocalDate, LocalTime, ZoneOffset>
    get() = Triple(
        toLocalDate(), toLocalTime(), offset
    )

/**
 * Get the [ZoneOffset] from the Clock's [ZoneId].
 */
val Clock.zoneOffset: ZoneOffset get() = zone.toZoneOffset()

/**
 * Return the time between the two DateTimes in the specified [ChronoUnit].
 * Non-directional (always non-negative).
 */
fun Temporal.timeBetween(other: Temporal, unit: ChronoUnit) = abs(unit.between(this, other))

/**
 * Return the seconds between the two DateTimes.
 * Non-directional (always non-negative).
 */
fun Temporal.secondsBetween(other: Temporal) = timeBetween(other, ChronoUnit.SECONDS)

/**
 * Return the seconds between the two LocalTimes.
 * Non-directional (always non-negative).
 *
 * if [laterTime] < [this], returns the seconds from [this] on day 1 to [laterTime] on day 2.
 */
fun LocalTime.secondsBetween(laterTime: LocalTime) = if (this < laterTime) {
    Duration.between(this, laterTime).seconds
} else {
    LocalDateTime.of(LocalDate.now(), this).secondsBetween(LocalDateTime.of(LocalDate.now().plusDays(1), laterTime))
}

/**
 * Get the [ZoneOffset] from a [ZoneId].
 */
fun ZoneId.toZoneOffset(): ZoneOffset = rules.getOffset(Instant.now())
