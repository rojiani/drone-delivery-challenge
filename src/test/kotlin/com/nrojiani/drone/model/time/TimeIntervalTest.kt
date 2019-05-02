package com.nrojiani.drone.model.time

import com.nrojiani.drone.model.DRONE_DELIVERY_OFF_HOURS
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.utils.DEFAULT_ZONE_OFFSET
import com.nrojiani.drone.utils.EST_ZONE_OFFSET
import com.nrojiani.drone.utils.daysToSeconds
import com.nrojiani.drone.utils.hoursToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeIntervalTest {
    private val startTime = LocalTime.parse("07:00:00").atOffset(DEFAULT_ZONE_OFFSET)
    private val positiveInterval =
        TimeInterval(start = startTime, duration = Duration.ofMinutes(30))
    private val singleDay = TimeInterval(start = startTime, duration = Duration.ofDays(1))
    private val day1 = LocalDate.of(2019, 5, 1)

    @Test
    fun seconds() {

        // start < end
        assertEquals(1800L, positiveInterval.seconds)
        assertEquals(daysToSeconds(1).toLong(), singleDay.seconds)
        assertEquals(hoursToSeconds(16), DRONE_DELIVERY_OPERATING_HOURS.seconds)

        // end < start (negative)
        val negativeInterval = TimeInterval(start = startTime, duration = Duration.ofMinutes(-30))
        assertEquals(-minsToSeconds(30), negativeInterval.seconds)
        assertEquals(hoursToSeconds(8), DRONE_DELIVERY_OFF_HOURS.seconds)

        // start == end
        val zeroInterval = TimeInterval(start = startTime, duration = Duration.ofSeconds(0))
        assertEquals(0, zeroInterval.seconds)
    }

    @Test
    fun contains() {
        val timeInRange = LocalTime.parse("07:15:00").atOffset(ZoneOffset.UTC)
        val timeNotInRange = LocalTime.parse("07:45:00").atOffset(ZoneOffset.UTC)
        assertTrue(positiveInterval.contains(timeInRange))
        assertTrue(timeInRange in positiveInterval)
        assertFalse(positiveInterval.contains(timeNotInRange))
        assertFalse(timeNotInRange in positiveInterval)
    }

    @Test
    fun secondaryConstructor() {
        assertEquals(
            positiveInterval,
            TimeInterval(
                start = LocalTime.of(7, 0, 0),
                duration = Duration.ofMinutes(30),
                offset = ZoneOffset.UTC
            )
        )
    }

    @Test
    fun `using different ZoneOffset`() {
        val intervalEST = TimeInterval(
            start = LocalTime.parse("07:00:00"),
            duration = Duration.ofMinutes(30),
            offset = EST_ZONE_OFFSET
        )

        assertEquals(DEFAULT_ZONE_OFFSET, positiveInterval.offset)
        assertEquals(EST_ZONE_OFFSET, intervalEST.offset)

        assertEquals(
            OffsetTime.of(LocalTime.of(7, 0, 0), EST_ZONE_OFFSET),
            intervalEST.start
        )

        assertEquals(
            OffsetTime.of(LocalTime.of(7, 30, 0), EST_ZONE_OFFSET),
            intervalEST.endExclusive
        )
    }

    @Test
    fun toZonedDateTimeInterval() {
        assertEquals(
            ZonedDateTimeInterval(
                start = ZonedDateTime.of(day1, startTime.toLocalTime(), DEFAULT_ZONE_OFFSET),
                duration = Duration.ofMinutes(30)
            ),
            positiveInterval.toZonedDateTimeInterval(startDate = day1, endDate = day1)
        )
    }

    @Test
    fun `toZonedDateTimeInterval - EST ZoneOffset`() {
        val day1 = LocalDate.of(2019, 5, 1)
        val day2 = day1.plusDays(1)

        val startTimeEST = LocalTime.parse("12:00:00").atOffset(EST_ZONE_OFFSET)
        val endTimeEST = LocalTime.parse("04:00:00").atOffset(EST_ZONE_OFFSET)
        val intervalEST = TimeInterval(start = startTimeEST, duration = Duration.ofHours(16))

        assertEquals(
            ZonedDateTimeInterval(
                start = ZonedDateTime.of(day1, startTimeEST.toLocalTime(), EST_ZONE_OFFSET),
                duration = Duration.ofHours(16)
            ),
            intervalEST.toZonedDateTimeInterval(startDate = day1, endDate = day2)
        )

        assertEquals(
            ZonedDateTimeInterval(
                start = ZonedDateTime.of(day1, startTimeEST.toLocalTime(), EST_ZONE_OFFSET),
                end = ZonedDateTime.of(day2, endTimeEST.toLocalTime(), EST_ZONE_OFFSET)
            ),
            intervalEST.toZonedDateTimeInterval(startDate = day1, endDate = day2)
        )
    }
}