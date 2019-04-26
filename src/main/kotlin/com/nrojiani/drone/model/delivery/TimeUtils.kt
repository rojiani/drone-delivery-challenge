@file:JvmName("TimeUtils")

package com.nrojiani.drone.model.delivery

import java.math.BigInteger

const val SECONDS_PER_MINUTE = 60
const val MINUTES_PER_HOUR = 60
const val HOURS_PER_DAY = 24

fun hoursToSeconds(hours: Int): Long {
    require(hours >= 0) { "hours must be non-negative: $hours" }
    return hours.toLong() * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
}

fun minsToSeconds(mins: Long): Long {
    require(mins >= 0) { "mins must be non-negative: $mins" }
    return mins * SECONDS_PER_MINUTE
}

fun daysToSeconds(days: Int): BigInteger {
    require(days >= 0) { "days must be non-negative: $days" }
    return days.toBigInteger() * (HOURS_PER_DAY.toBigInteger() *
            MINUTES_PER_HOUR.toBigInteger() *
            SECONDS_PER_MINUTE.toBigInteger())
}
