package com.nrojiani.drone.utils.extensions

import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeExtensionsTest {

    private val time1 = LocalTime.parse("06:31:21")
    private val time2 = LocalTime.parse("20:05:34")
    private val dateTime1: LocalDateTime = LocalDateTime.of(LocalDate.of(2019, 4, 26), time1)
    private val dateTime2: LocalDateTime = LocalDateTime.of(LocalDate.of(2019, 4, 26), time2)
    private val dateTime3: LocalDateTime = LocalDateTime.of(LocalDate.of(2019, 4, 29), LocalTime.parse("12:00:00"))

    @Test
    fun isSameDayAs() {
        assertTrue(LocalDateTime.now().isSameDayAs(LocalDateTime.now()))
        assertTrue(
            LocalDateTime.of(LocalDate.of(2019, 4, 25), time1)
                .isSameDayAs(LocalDateTime.of(LocalDate.parse("2019-04-25"), time1))
        )
        assertTrue(
            LocalDateTime.of(LocalDate.of(2019, 4, 25), time1)
                .isSameDayAs(LocalDateTime.of(LocalDate.of(2019, 4, 25), time2))
        )
        assertFalse(
            LocalDateTime.of(LocalDate.of(2019, 4, 25), time1)
                .isSameDayAs(LocalDateTime.of(LocalDate.of(2019, 4, 24), time2))
        )
    }

    @Test
    fun dateAndTime() {
        val dateTime = LocalDateTime.of(LocalDate.parse("2019-04-26"), time1)
        val (date, time) = dateTime.dateAndTime
        assertEquals(LocalDate.parse("2019-04-26"), date)
        assertEquals(time1, time)
    }

    @Test
    fun timeBetween() {
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
    fun `LocalDateTime - secondsBetween`() {
        assertEquals(48853, dateTime1.secondsBetween(dateTime2))
        assertEquals(278919, dateTime1.secondsBetween(dateTime3))
    }

    @Test
    fun `LocalTime - secondsBetween`() {
        assertEquals(48853, time1.secondsBetween(time2))
        assertEquals(1, LocalTime.parse("23:59:59").secondsBetween(LocalTime.parse("00:00:00")))
    }
}