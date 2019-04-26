@file:JvmName("OrderParser")

package com.nrojiani.drone.io.parser

import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.order.Order
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException

/**
 * Converts the input file at [filepath] to a list of [Order].
 */
internal fun parseOrdersFromFile(filepath: String): List<Order> = readFileLines(filepath).run(::parseOrders)

/**
 * Map each order input line to an [Order] model object.
 */
internal fun parseOrders(orderLines: List<String>): List<Order> = orderLines.map { parseOrder(it) }

/**
 * Responsible for parsing a line of input and mapping the
 * input representation into a domain model object ([Order]).
 */
internal fun parseOrder(orderInput: String): Order {
    val components = orderInput.split(" ")
    require(components.size == 3) { "Unrecognized input format: $orderInput" }

    val (orderId, rawCoordinates, timestamp) = components

    val coordinates = parseRawCoordinates(rawCoordinates)
    val time = parseTimestamp(timestamp)
    // Bundle time with today's date (for rollover)
    val dateTime = LocalDateTime.of(LocalDate.now(), time)

    return Order(orderId, coordinates, dateTime)
}

/**
 * Convert the String representation of a customer's grid coordinates ("N15W9")
 * to a [Coordinate].
 */
internal fun parseRawCoordinates(rawCoordinates: String): Coordinate =
    Regex("""([NS])([0-9]+)([EW])([0-9]+)""").matchEntire(rawCoordinates)
        ?.destructured
        ?.let { (latitudeSymbol, latitude, longitudeSymbol, longitude) ->

            // Validate
            require(longitudeSymbol in setOf("E", "W") && latitudeSymbol in setOf("N", "S")) {
                "Bad input: $rawCoordinates"
            }

            val x = when (longitudeSymbol) {
                "E" -> longitude.toDouble()
                else -> longitude.toDouble() * -1
            }
            val y = when (latitudeSymbol) {
                "N" -> latitude.toDouble()
                else -> latitude.toDouble() * -1
            }

            return Coordinate(x, y)
        } ?: throw IllegalArgumentException("Bad input: $rawCoordinates")

/**
 * Create a [LocalTime] object from the [timestamp].
 * @param timestamp with expected format `HH:MM:SS`
 * @throws DateTimeParseException if timestamp can't be parsed
 */
internal fun parseTimestamp(timestamp: String): LocalTime = LocalTime.parse(timestamp)