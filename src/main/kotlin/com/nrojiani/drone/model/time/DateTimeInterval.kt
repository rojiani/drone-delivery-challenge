package com.nrojiani.drone.model.time

import com.nrojiani.drone.utils.extensions.secondsBetween
import java.time.Duration
import java.time.LocalDateTime

data class DateTimeInterval(val start: LocalDateTime, val duration: Duration) {

    val endExclusive: LocalDateTime = start.plus(duration)

    /**
     * The number of seconds from [start] until [endExclusive].
     * Non-directional (always non-negative).
     */
    val seconds: Long get() = start.secondsBetween(endExclusive)

    operator fun contains(time: LocalDateTime): Boolean {
        val a = if (start <= endExclusive) start else endExclusive
        val b = if (start > endExclusive) start else endExclusive

        return (a <= time) && (time <= b)
    }
}
