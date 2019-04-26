package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.DRONE_DELIVERY_OFF_HOURS
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertFailsWith

class ShortTimeIntervalTest {

    private val startBeforeEnd1 = ShortTimeInterval(LocalTime.of(7, 0, 0), LocalTime.of(7, 30, 0))
    private val startBeforeEnd2 = ShortTimeInterval(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59))

    @Test
    fun seconds() {
        // start < end
        assertEquals(1800L, startBeforeEnd1.seconds)
        assertEquals(daysToSeconds(1).toLong() - 1L, startBeforeEnd2.seconds)
        assertEquals(hoursToSeconds(16), DRONE_DELIVERY_OPERATING_HOURS.seconds)

        // end < start (overnight)
        assertEquals(hoursToSeconds(8), DRONE_DELIVERY_OFF_HOURS.seconds)

        // start == end
        assertFailsWith<IllegalArgumentException> { ShortTimeInterval(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0)) }
    }
}