@file:JvmName("DateTimeExtensions")

package com.nrojiani.drone.utils.extensions

import com.nrojiani.drone.model.delivery.ShortTimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

fun LocalTime.isInShortTimeInterval(shortInterval: ShortTimeInterval): Boolean = when {
    this == shortInterval.start || this == shortInterval.end -> true
    this.isAfter(shortInterval.start) && this.isBefore(shortInterval.end) -> true
    else -> false
}
