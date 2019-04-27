@file:JvmName("DateTimeExtensions")

package com.nrojiani.drone.utils.extensions

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/*
 * This file contains extension functions for the Java 8 Date/Time API (LocalDateTime, LocalTime, LocalDate, etc.).
 *
 * Note: in a "real-world" project, it would be preferable to keep extension functions
 * like this in a separate library.
 */

/**
 * Returns the [LocalDate] and [LocalTime] as a Pair
 *
 * Example:
 * ```
 * val (date, time) = myLocalDateTime.dateAndTime
 * ```
 */
val LocalDateTime.dateAndTime: Pair<LocalDate, LocalTime> get() = toLocalDate() to toLocalTime()

/**
 * Return true if [other] has the same Date as the receiver.
 */
fun LocalDateTime.isSameDayAs(other: LocalDateTime): Boolean = this.toLocalDate().isEqual(other.toLocalDate())

/**
 * Return the time between the two DateTimes in the specified [ChronoUnit].
 * Non-directional (always non-negative).
 */
fun LocalDateTime.timeBetween(other: LocalDateTime, unit: ChronoUnit) = abs(unit.between(this, other))

/**
 * Return the seconds between the two DateTimes.
 * Non-directional (always non-negative).
 */
fun LocalDateTime.secondsBetween(other: LocalDateTime) = timeBetween(other, ChronoUnit.SECONDS)

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
