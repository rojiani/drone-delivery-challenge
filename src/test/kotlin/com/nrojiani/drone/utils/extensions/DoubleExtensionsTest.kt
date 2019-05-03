package com.nrojiani.drone.utils.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtensionsTest {
    @Test
    fun formatToNDecimalPlaces() {
        assertEquals("1235", (1234.5678).formatToNDecimalPlaces(0))
        assertEquals("1234.6", (1234.5678).formatToNDecimalPlaces(1))
        assertEquals("1234.57", (1234.5678).formatToNDecimalPlaces(2))
        assertEquals("1234.568", (1234.5678).formatToNDecimalPlaces(3))
        assertEquals("1234.5678", (1234.5678).formatToNDecimalPlaces(4))
    }
}