@file:JvmName("DateTimeExtensions")
/**
 * This file contains extension functions for the Java 8 Date/Time API ([LocalDateTime], [LocalTime], [LocalDate],
 * etc.).
 *
 * Note: in a "real-world" project, it would be preferable to keep extension functions
 * like this in a separate library.
 */
package com.nrojiani.drone.extensions

import com.nrojiani.drone.model.delivery.ShortTimeInterval
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Return true if [other] has the same Date as the receiver.
 */
fun LocalDateTime.isSameDayAs(other: LocalDateTime): Boolean = this.toLocalDate().isEqual(other.toLocalDate())

fun LocalTime.isInTimeInterval(intervalShort: ShortTimeInterval): Boolean = when {
    this.equals(intervalShort.start) || this.equals(intervalShort.end) -> true
    this.isAfter(intervalShort.start) && this.isBefore(intervalShort.end) -> true
    else -> false
}
