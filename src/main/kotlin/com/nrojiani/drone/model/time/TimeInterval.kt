package com.nrojiani.drone.model.time

import java.time.*

/**
 * Time interval with zoned times (date agnostic).
 */
data class TimeInterval(
    val start: OffsetTime,
    val duration: Duration
) {
    val endExclusive: OffsetTime = start.plus(duration)
    val offset: ZoneOffset = start.offset

    /**
     * Alternate constructor.
     */
    constructor(start: LocalTime, duration: Duration, offset: ZoneOffset) : this(start.atOffset(offset), duration)

    /**
     * The number of seconds from [start] until [endExclusive].
     * Non-directional (always non-negative).
     */
    val seconds: Long get() = duration.seconds

    operator fun contains(time: OffsetTime): Boolean {
        val a = if (start <= endExclusive) start else endExclusive
        val b = if (start > endExclusive) start else endExclusive

        return (a <= time) && (time <= b)
    }

    fun toZonedDateTimeInterval(startDate: LocalDate, endDate: LocalDate): ZonedDateTimeInterval =
        ZonedDateTimeInterval(
            start = ZonedDateTime.of(startDate, start.toLocalTime(), offset),
            end = ZonedDateTime.of(endDate, endExclusive.toLocalTime(), offset)
        )
}
