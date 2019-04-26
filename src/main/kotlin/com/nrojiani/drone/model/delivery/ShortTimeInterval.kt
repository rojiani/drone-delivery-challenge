package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.utils.daysToSeconds
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * Represents a Time interval with a max of 23:59:59 between the start & end.
 * `ShortTimeInterval(start = 09:00:00, end = 12:00:00)` represents 9am to 12pm (on the same day).
 * `ShortTimeInterval(start = 22:00:00, end = 06:00:00)` represents 10pm to 6am the following day.
 */
data class ShortTimeInterval(val start: LocalTime, val end: LocalTime) {
    /**
     * The number of seconds from [start] to [end]
     */
    val seconds: Long
        get() =
            when {
                start.isBefore(end) -> start.until(end, ChronoUnit.SECONDS)
                else -> daysToSeconds(1).toLong() - end.until(start, ChronoUnit.SECONDS)
            }

    init {
        // if start == end, then it could represent either an empty interval or a 24-hour interval.
        require(start != end) { "start cannot be equal to end" }
    }
}
