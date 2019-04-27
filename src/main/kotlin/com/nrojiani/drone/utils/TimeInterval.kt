package com.nrojiani.drone.utils

import java.time.Duration
import java.time.LocalTime

data class TimeInterval(val start: LocalTime, val duration: Duration) {

    val endExclusive: LocalTime = start.plus(duration)

    /**
     * The number of seconds from [start] until [endExclusive].
     * Non-directional (always non-negative).
     */
    val seconds: Long get() = duration.seconds

    operator fun contains(time: LocalTime): Boolean {
        val a = if (start <= endExclusive) start else endExclusive
        val b = if (start > endExclusive) start else endExclusive

        return (a <= time) && (time <= b)
    }
}
