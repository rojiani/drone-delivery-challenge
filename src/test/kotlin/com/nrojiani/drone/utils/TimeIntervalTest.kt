package com.nrojiani.drone.utils

import com.nrojiani.drone.model.DRONE_DELIVERY_OFF_HOURS
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.LocalTime
import kotlin.test.assertFalse

class TimeIntervalTest {
    private val startTime = LocalTime.parse("07:00:00")
    private val positiveInterval = TimeInterval(start = startTime, duration = Duration.ofMinutes(30))
    private val zeroInterval = TimeInterval(start = startTime, duration = Duration.ofSeconds(0))
    private val singleDay = TimeInterval(start = startTime, duration = Duration.ofDays(1))
    private val negativeInterval = TimeInterval(start = startTime, duration = Duration.ofMinutes(-30))

    @Test
    fun seconds() {
        // start < end
        assertEquals(1800L, positiveInterval.seconds)
        assertEquals(daysToSeconds(1).toLong(), singleDay.seconds)
        assertEquals(hoursToSeconds(16), DRONE_DELIVERY_OPERATING_HOURS.seconds)

        // end < start (negative)
        assertEquals(-minsToSeconds(30), negativeInterval.seconds)
        assertEquals(hoursToSeconds(8), DRONE_DELIVERY_OFF_HOURS.seconds)

        // start == end
        assertEquals(0, zeroInterval.seconds)
    }

    @Test
    fun contains() {
        val timeInRange = LocalTime.parse("07:15:00")
        val timeNotInRange = LocalTime.parse("07:45:00")
        assertTrue(positiveInterval.contains(timeInRange))
        assertTrue(timeInRange in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRange))
        assertFalse(timeNotInRange in positiveInterval)
    }
}