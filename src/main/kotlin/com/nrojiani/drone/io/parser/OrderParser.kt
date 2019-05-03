@file:JvmName("OrderParser")

package com.nrojiani.drone.io.parser

import arrow.core.Try
import arrow.core.getOrElse
import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.utils.UTC_ZONE_ID
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

/**
 * Converts the input file at [filepath] to a list of [Order].
 */
internal fun parseOrdersFromFile(filepath: String, exitOnInvalidInput: Boolean = true): List<Order> = when {
    exitOnInvalidInput -> readFileLines(filepath).run(::parseOrders)
    else -> readFileLines(filepath).run(::parseOrdersSkippingInvalid)
}

/**
 * Map each order input line to an [Order] model object.
 * @throws OrderParsingException if invalid input in [orderLines].
 */
internal fun parseOrders(orderLines: List<String>): List<Order> = orderLines.map { parseOrder(it) }

/**
 * Map each order input line to an [Order] model object, ignoring invalid input.
 */
internal fun parseOrdersSkippingInvalid(orderLines: List<String>): List<Order> =
    orderLines.fold(listOf<Order?>()) { acc, line ->
        acc + tryParseOrder(line).getOrElse { null }
    }.filterNotNull()

internal fun tryParseOrder(orderInput: String): Try<Order> = Try { parseOrder(orderInput) }

/**
 * Responsible for parsing a line of input and mapping the
 * input representation into a domain model object ([Order]).
 * OrderParsingException if the [orderInput] does not have the expected format.
 */
internal fun parseOrder(orderInput: String): Order {
    val components = orderInput.split(" ")
    require(components.size == 3) { "Unrecognized input format: $orderInput" }
    val (orderId, rawCoordinates, timestamp) = components

    val coordinates: Coordinate = tryParseCoordinates(rawCoordinates)
        .fold(
            { e -> throw OrderParsingException("Unable to parse coordinates in order input: $rawCoordinates", e) },
            { it }
        )

    val time: LocalTime = parseTimestamp(timestamp)
        .fold(
            { e -> throw OrderParsingException("Unable to parse timestamp in order input: $timestamp", e) },
            { it }
        )

    // Bundle time with today's date (for rollover)
    // TODO - optimally the input should have both date & time
    val dateTime = ZonedDateTime.of(LocalDate.now(), time, UTC_ZONE_ID)

    return Order(orderId, coordinates, dateTime)
}

/**
 * Convert the String representation of a customer's grid coordinates ("N15W9")
 * to a [Coordinate]. If the input coordinates are not well formed, the Try will contain
 * an [IllegalArgumentException].
 */
internal fun tryParseCoordinates(rawCoordinates: String): Try<Coordinate> = Try { parseRawCoordinates(rawCoordinates) }

/**
 * Convert the String representation of a customer's grid coordinates ("N15W9")
 * to a [Coordinate].
 * @throws IllegalArgumentException if the input coordinates are not well formed.
 */
@Throws(IllegalArgumentException::class)
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
@Throws(DateTimeParseException::class)
internal fun parseTimestamp(timestamp: String): Try<LocalTime> = Try { LocalTime.parse(timestamp) }