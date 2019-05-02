package com.nrojiani.drone.model.time

import com.nrojiani.drone.utils.daysToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZonedDateTimeIntervalTest {
    private val startDate = LocalDate.of(2019, 4, 26)
    private val positiveInterval = ZonedDateTimeInterval(
        start = ZonedDateTime.of(startDate, LocalTime.parse("07:00:00"), UTC_ZONE_ID),
        duration = Duration.ofMinutes(30)
    )
    private val zeroInterval = ZonedDateTimeInterval(
        start = ZonedDateTime.of(startDate, LocalTime.parse("07:00:00"), UTC_ZONE_ID),
        duration = Duration.ofSeconds(0)
    )
    private val singleDay = ZonedDateTimeInterval(
        start = ZonedDateTime.of(startDate, LocalTime.parse("00:00:00"), UTC_ZONE_ID),
        duration = Duration.ofDays(1)
    )
    private val negativeInterval = ZonedDateTimeInterval(
        start = ZonedDateTime.of(startDate, LocalTime.parse("07:30:00"), UTC_ZONE_ID),
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
        val timeInRange = ZonedDateTime.of(startDate, LocalTime.parse("07:15:00"), UTC_ZONE_ID)
        val timeNotInRange = ZonedDateTime.of(startDate, LocalTime.parse("07:45:00"), UTC_ZONE_ID)
        assertTrue(positiveInterval.contains(timeInRange))
        assertTrue(timeInRange in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRange))
        assertFalse(timeNotInRange in positiveInterval)
    }
}
