package com.nrojiani.drone.model.time

import com.nrojiani.drone.utils.daysToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertFalse

class DateTimeIntervalTest {
    private val startDate = LocalDate.of(2019, 4, 26)
    private val positiveInterval = DateTimeInterval(
        start = LocalDateTime.of(startDate, LocalTime.parse("07:00:00")),
        duration = Duration.ofMinutes(30)
    )
    private val zeroInterval = DateTimeInterval(
        start = LocalDateTime.of(startDate, LocalTime.parse("07:00:00")),
        duration = Duration.ofSeconds(0)
    )
    private val singleDay = DateTimeInterval(
        start = LocalDateTime.of(startDate, LocalTime.parse("00:00:00")),
        duration = Duration.ofDays(1)
    )
    private val negativeInterval = DateTimeInterval(
        start = LocalDateTime.of(startDate, LocalTime.parse("07:30:00")),
        duration = Duration.ofMinutes(-30)
    )

    @Test
    fun seconds() {
        // start < end
        assertEquals(1800L, positiveInterval.seconds)
        assertEquals(daysToSeconds(1).toLong(), singleDay.seconds)

        // end < start (negative)
        assertEquals(minsToSeconds(30), negativeInterval.seconds)
        assertEquals(-minsToSeconds(30), negativeInterval.duration.seconds)

        // start == end
        assertEquals(0, zeroInterval.seconds)
    }

    @Test
    fun contains() {
        val timeInRange = LocalDateTime.of(startDate, LocalTime.parse("07:15:00"))
        val timeNotInRange = LocalDateTime.of(startDate, LocalTime.parse("07:45:00"))
        assertTrue(positiveInterval.contains(timeInRange))
        assertTrue(timeInRange in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRange))
        assertFalse(timeNotInRange in positiveInterval)
    }
}