package com.nrojiani.drone.parser

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.Order
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException
import kotlin.test.assertFailsWith

class OrderParserTest {

    @Test
    fun parseOrders() {
        assertEquals(
            listOf(
                Order(
                    orderId = "WM001",
                    destination = Coordinate(-5, 11),
                    dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 11, 50))
                ),
                Order(
                    orderId = "WM002",
                    destination = Coordinate(2, -3),
                    dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 11, 55))
                )
            ), parseOrders(listOf("WM001 N11W5 05:11:50", "WM002 S3E2 05:11:55"))
        )
    }

    @Test
    fun parseOrder() {
        assertEquals(
            Order(
                orderId = "WM001",
                destination = Coordinate(-5, 11),
                dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 11, 50))
            ), parseOrder("WM001 N11W5 05:11:50")
        )
    }

    @Test
    fun parseRawCoordinates() {
        assertEquals(Coordinate(-5, 11), parseRawCoordinates("N11W5"))
        assertEquals(Coordinate(2, -3), parseRawCoordinates("S3E2"))
        assertEquals(Coordinate(50, 7), parseRawCoordinates("N7E50"))
        assertEquals(Coordinate(5, 11), parseRawCoordinates("N11E5"))
    }

    @Test
    fun parseRawCoordinates_withInvalidInput_throwsException() {
        // Latitude before Longitude
        assertFailsWith<IllegalArgumentException> {
            parseRawCoordinates("E5N11")
        }

        // Not N|S & E|W
        assertFailsWith<IllegalArgumentException> {
            parseRawCoordinates("F5Z11")
        }

        // Trailing space
        assertFailsWith<IllegalArgumentException> {
            parseRawCoordinates("N11W5 ")
        }

        // Non-integer numbers
        assertFailsWith<IllegalArgumentException> {
            parseRawCoordinates("N1.1W2.9")
        }
    }

    @Test
    fun parseTimestamp() {
        assertEquals(LocalTime.of(0, 11, 50), parseTimestamp("00:11:50"))
        assertEquals(LocalTime.of(5, 11, 50), parseTimestamp("05:11:50"))
        assertEquals(LocalTime.of(13, 11, 50), parseTimestamp("13:11:50"))
        assertEquals(LocalTime.of(23, 11, 50), parseTimestamp("23:11:50"))
    }

    @Test
    fun parseTimestamp_withInvalidInput_throwsException() {
        assertFailsWith<DateTimeParseException> {
            parseTimestamp("24:11:50")
        }

        assertFailsWith<DateTimeParseException> {
            parseTimestamp("0:11:50")
        }

        assertFailsWith<DateTimeParseException> {
            parseTimestamp("-01:11:50")
        }
    }
}