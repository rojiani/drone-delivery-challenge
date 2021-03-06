package com.nrojiani.drone.model.time

import com.nrojiani.drone.utils.extensions.secondsBetween
import java.time.Duration
import java.time.ZonedDateTime

data class ZonedDateTimeInterval(val start: ZonedDateTime, val duration: Duration) {

    val endExclusive: ZonedDateTime = start.plus(duration)

    /**
     * The number of seconds from [start] until [endExclusive].
     * Non-directional (always non-negative).
     */
    val seconds: Long get() = start.secondsBetween(endExclusive)

    /** Alternate constructor */
    constructor(start: ZonedDateTime, end: ZonedDateTime) : this(start, Duration.between(start, end))

    operator fun contains(time: ZonedDateTime): Boolean {
        val a = if (start <= endExclusive) start else endExclusive
        val b = if (start > endExclusive) start else endExclusive

        return (a <= time) && (time <= b)
    }
}
