package com.nrojiani.drone.extensions

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertFalse

class DateTimeExtensionsTest {

    private val time1 = LocalTime.parse("06:31:21")
    private val time2 = LocalTime.parse("20:05:34")
    private val timeBeforeOperatingHours = LocalTime.parse("05:59:59")
    private val timeAfterOperatingHours = LocalTime.parse("22:00:01")

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
    fun isInTimeInterval() {
        assertTrue(time1.isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS))
        assertTrue(time2.isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS))
        assertTrue(
            DRONE_DELIVERY_OPERATING_HOURS.start
                .isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS)
        )
        assertTrue(
            DRONE_DELIVERY_OPERATING_HOURS.end
                .isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS)
        )
        assertFalse(
            timeBeforeOperatingHours
                .isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS)
        )
        assertFalse(
            timeAfterOperatingHours
                .isInTimeInterval(DRONE_DELIVERY_OPERATING_HOURS)
        )
    }
}