package com.nrojiani.drone.utils.extensions

import com.nrojiani.drone.model.time.UTC_ZONE_ID
import com.nrojiani.drone.utils.hoursToSeconds
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class DateTimeExtensionsTest {

    private val time1 = LocalTime.parse("06:31:21")
    private val time2 = LocalTime.parse("20:05:34")
    private val time3 = LocalTime.parse("12:00:00")
    private val date1 = LocalDate.of(2019, 4, 26)
    private val date2 = LocalDate.of(2019, 4, 29)
    private val dateTime1 = LocalDateTime.of(date1, time1)
    private val dateTime2 = LocalDateTime.of(date1, time2)
    private val dateTime3 = LocalDateTime.of(date2, time3)
    private val zonedDateTime1 = ZonedDateTime.of(date1, time1, UTC_ZONE_ID)
    private val zonedDateTime2 = ZonedDateTime.of(date1, time2, UTC_ZONE_ID)
    private val zonedDateTime3 = ZonedDateTime.of(date2, time3, UTC_ZONE_ID)

    @Test
    fun `dateAndTime - LocalDateTime`() {
        val dateTime = LocalDateTime.of(LocalDate.parse("2019-04-26"), time1)
        val (date, time) = dateTime.dateAndTime
        assertEquals(LocalDate.parse("2019-04-26"), date)
        assertEquals(time1, time)
    }

    @Test
    fun `dateAndTime - ZonedDateTime`() {
        val (date, time) = zonedDateTime1.dateAndTime
        assertEquals(LocalDate.parse("2019-04-26"), date)
        assertEquals(time1, time)
    }

    @Test
    fun `timeBetween - LocalDateTime`() {
        // Same day
        assertEquals(48853, dateTime1.timeBetween(dateTime2, ChronoUnit.SECONDS))
        assertEquals(814, dateTime1.timeBetween(dateTime2, ChronoUnit.MINUTES))
        assertEquals(13, dateTime1.timeBetween(dateTime2, ChronoUnit.HOURS))

        // diff days
        assertEquals(278919, dateTime1.timeBetween(dateTime3, ChronoUnit.SECONDS))
        assertEquals(4648, dateTime1.timeBetween(dateTime3, ChronoUnit.MINUTES))
        assertEquals(77, dateTime1.timeBetween(dateTime3, ChronoUnit.HOURS))
    }

    @Test
    fun `timeBetween - ZonedDateTime`() {
        // Same day
        assertEquals(48853, zonedDateTime1.timeBetween(zonedDateTime2, ChronoUnit.SECONDS))
        assertEquals(814, zonedDateTime1.timeBetween(zonedDateTime2, ChronoUnit.MINUTES))
        assertEquals(13, zonedDateTime1.timeBetween(zonedDateTime2, ChronoUnit.HOURS))

        // diff days
        assertEquals(278919, zonedDateTime1.timeBetween(zonedDateTime3, ChronoUnit.SECONDS))
        assertEquals(4648, zonedDateTime1.timeBetween(zonedDateTime3, ChronoUnit.MINUTES))
        assertEquals(77, zonedDateTime1.timeBetween(zonedDateTime3, ChronoUnit.HOURS))

        // diff zones (EST = UTC-5)
        assertEquals(
            5,
            ZonedDateTime.of(date1, time3, UTC_ZONE_ID)
                .timeBetween(
                    ZonedDateTime.of(date1, time3, ZoneId.of("EST", ZoneId.SHORT_IDS)),
                    ChronoUnit.HOURS
                )
        )
    }

    @Test
    fun `secondsBetween - Temporal`() {
        assertEquals(48853, dateTime1.secondsBetween(dateTime2))
        assertEquals(278919, dateTime1.secondsBetween(dateTime3))

        assertEquals(
            hoursToSeconds(5),
            ZonedDateTime.of(date1, time3, UTC_ZONE_ID)
                .secondsBetween(ZonedDateTime.of(date1, time3, ZoneId.of("EST", ZoneId.SHORT_IDS)))
        )
    }

    @Test
    fun `secondsBetween - LocalTime`() {
        assertEquals(48853, time1.secondsBetween(time2))
        assertEquals(1, LocalTime.parse("23:59:59").secondsBetween(LocalTime.parse("00:00:00")))
    }
}