package com.nrojiani.drone.io.parser

import arrow.core.Try
import arrow.core.handleError
import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.testutils.TEST_INPUT_FILEPATH
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.testutils.Test1OrderData.ORDERS
import com.nrojiani.drone.utils.UTC_ZONE_ID
import org.junit.Test
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrderParserTest {

    @Test
    fun parseOrdersFromFile() {
        assertEquals(ORDERS, parseOrdersFromFile(TEST_INPUT_FILEPATH))
    }

    @Test
    fun parseValidOrders() {
        val orders =
            listOf("WM001 N11W5 05:11:50", "WM002 S3E2 05:11:55", "WM998 W5N11 03:01:22", "WM998 W5N11 03:01:F")
        val valid = parseValidOrders(orders)
        assertEquals(ORDERS.take(2), valid)
    }

    @Test
    fun parseOrders() {
        assertEquals(
            ORDERS.take(2),
            parseOrders(listOf("WM001 N11W5 05:11:50", "WM002 S3E2 05:11:55"))
        )
    }

    @Test
    fun parseOrder() {
        assertEquals(
            Order(
                orderId = "WM001",
                destination = Coordinate(-5, 11),
                orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.of(5, 11, 50), UTC_ZONE_ID)
            ), parseOrder("WM001 N11W5 05:11:50")
        )
    }

    @Test
    fun `parseOrder with invalid coordinate`() {
        assertFailsWith<OrderParsingException> {
            parseOrder("WM001 E5N11 05:11:50")
        }
    }

    @Test
    fun `parseOrder with invalid timestamp`() {
        assertFailsWith<OrderParsingException> {
            parseOrder("WM001 N11W5 5:1:50")
        }
    }

    @Test
    fun tryParseCoordinates() {
        assertEquals(Try.Success(Coordinate(-5, 11)), tryParseCoordinates("N11W5"))
        assertEquals(Try.Success(Coordinate(2, -3)), tryParseCoordinates("S3E2"))
        assertEquals(Try.Success(Coordinate(50, 7)), tryParseCoordinates("N7E50"))
        assertEquals(Try.Success(Coordinate(5, 11)), tryParseCoordinates("N11E5"))

        assertFalse(tryParseCoordinates("N11E5").isFailure())
        assertTrue(tryParseCoordinates("E5N11").isFailure())
    }

    @Test
    fun parseRawCoordinates() {
        assertEquals(Coordinate(-5, 11), parseRawCoordinates("N11W5"))
        assertEquals(Coordinate(2, -3), parseRawCoordinates("S3E2"))
        assertEquals(Coordinate(50, 7), parseRawCoordinates("N7E50"))
        assertEquals(Coordinate(5, 11), parseRawCoordinates("N11E5"))
    }

    @Test
    fun `parseRawCoordinates with invalid input throws IllegalArgumentException`() {
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
        assertEquals(Try.Success(LocalTime.of(0, 11, 50)), parseTimestamp("00:11:50"))
        assertEquals(Try.Success(LocalTime.of(5, 11, 50)), parseTimestamp("05:11:50"))
        assertEquals(Try.Success(LocalTime.of(13, 11, 50)), parseTimestamp("13:11:50"))
        assertEquals(Try.Success(LocalTime.of(23, 11, 50)), parseTimestamp("23:11:50"))
    }

    @Test
    fun `parseTimestamp with invalid input throws DateTimeParseException`() {
        assertTrue(parseTimestamp("24:11:50").isFailure())
        assertTrue(parseTimestamp("0:11:50").isFailure())
        assertTrue(parseTimestamp("-01:11:50").isFailure())

        assertFailsWith<DateTimeParseException> {
            parseTimestamp("24:11:50").handleError { t ->
                throw t
            }
        }
    }
}