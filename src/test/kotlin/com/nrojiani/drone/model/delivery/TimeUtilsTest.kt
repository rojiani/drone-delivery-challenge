package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.utils.daysToSeconds
import com.nrojiani.drone.utils.hoursToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Test

import org.junit.Assert.assertEquals
import java.lang.IllegalArgumentException
import java.math.BigInteger
import kotlin.test.assertFailsWith

class TimeUtilsTest {

    @Test
    fun hoursToSeconds() {
        assertEquals(0L, hoursToSeconds(0))
        assertEquals(3600L, hoursToSeconds(1))
        assertEquals(7200L, hoursToSeconds(2))
        assertEquals(28800L, hoursToSeconds(8))
        assertEquals(57600L, hoursToSeconds(16))
        assertEquals(8053200L, hoursToSeconds(2237))

        assertFailsWith<IllegalArgumentException> { hoursToSeconds(-1) }
    }

    @Test
    fun minsToSeconds() {
        assertEquals(0L, minsToSeconds(0L))
        assertEquals(60L, minsToSeconds(1L))
        assertEquals(120L, minsToSeconds(2L))
        assertEquals(134220L, minsToSeconds(2237L))

        assertFailsWith<IllegalArgumentException> { minsToSeconds(-1L) }
    }

    @Test
    fun daysToSeconds() {
        assertEquals(BigInteger.ZERO, daysToSeconds(0))
        assertEquals(BigInteger("86400"), daysToSeconds(1))
        assertEquals(BigInteger("172800"), daysToSeconds(2))
        assertEquals(BigInteger("2592000"), daysToSeconds(30))
        assertEquals(BigInteger("31536000"), daysToSeconds(365))

        assertFailsWith<IllegalArgumentException> { daysToSeconds(-1) }
    }
}