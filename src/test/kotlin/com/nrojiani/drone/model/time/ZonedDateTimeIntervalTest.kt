package com.nrojiani.drone.model.time

import com.nrojiani.drone.utils.EST_ZONE_OFFSET
import com.nrojiani.drone.utils.UTC_ZONE_ID
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

    private val timeInRangeUTC = ZonedDateTime.of(startDate, LocalTime.parse("07:15:00"), UTC_ZONE_ID)
    private val timeNotInRangeUTC = ZonedDateTime.of(startDate, LocalTime.parse("07:45:00"), UTC_ZONE_ID)

    @Test
    fun seconds() {
        val zeroInterval = ZonedDateTimeInterval(
            start = ZonedDateTime.of(startDate, LocalTime.parse("07:00:00"), UTC_ZONE_ID),
            duration = Duration.ofSeconds(0)
        )
        val singleDay = ZonedDateTimeInterval(
            start = ZonedDateTime.of(startDate, LocalTime.parse("00:00:00"), UTC_ZONE_ID),
            duration = Duration.ofDays(1)
        )
        val negativeInterval = ZonedDateTimeInterval(
            start = ZonedDateTime.of(startDate, LocalTime.parse("07:30:00"), UTC_ZONE_ID),
            duration = Duration.ofMinutes(-30)
        )

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
        assertTrue(positiveInterval.contains(timeInRangeUTC))
        assertTrue(timeInRangeUTC in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRangeUTC))
        assertFalse(timeNotInRangeUTC in positiveInterval)
    }

    @Test
    fun `contains - different ZoneOffsets`() {
        val timeInRangeEST = ZonedDateTime.of(startDate, LocalTime.parse("02:15:00"), EST_ZONE_OFFSET)
        val timeNotInRangeEST = ZonedDateTime.of(startDate, LocalTime.parse("02:45:00"), EST_ZONE_OFFSET)

        // Make sure EST times equivalent to UTC
        assertEquals(timeInRangeUTC.toLocalDate(), timeInRangeEST.toLocalDate())
        assertEquals(timeInRangeUTC.toLocalTime().minusHours(5), timeInRangeEST.toLocalTime())
        assertEquals(timeNotInRangeUTC.toLocalDate(), timeNotInRangeEST.toLocalDate())
        assertEquals(timeNotInRangeUTC.toLocalTime().minusHours(5), timeNotInRangeEST.toLocalTime())

        // Check if UTC interval contains an EST Time
        assertTrue(positiveInterval.contains(timeInRangeUTC))
        assertTrue(positiveInterval.contains(timeInRangeEST))
        assertTrue(timeInRangeUTC in positiveInterval)
        assertTrue(timeInRangeEST in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRangeUTC))
        assertFalse(positiveInterval.contains(timeNotInRangeEST))
        assertFalse(timeNotInRangeUTC in positiveInterval)
        assertFalse(timeNotInRangeEST in positiveInterval)
    }

    @Test
    fun secondaryConstructor() {
        val interval = ZonedDateTimeInterval(
            start = ZonedDateTime.of(startDate, LocalTime.parse("07:00:00"), UTC_ZONE_ID),
            end = ZonedDateTime.of(startDate, LocalTime.parse("07:30:00"), UTC_ZONE_ID)
        )
        assertEquals(30, interval.duration.toMinutes())
        assertEquals(positiveInterval, interval)
    }
}
