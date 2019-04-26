package com.nrojiani.drone.testutils

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.model.delivery.TransitTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Maximum tolerated difference when comparing floating-point types for equality. */
const val EPSILON = 1e-10

/** The ORIGIN on the coordinate system. */
@JvmField
val ORIGIN = Coordinate(0, 0)

/** Absolute filepath to sample test input. */
const val TEST_INPUT_FILEPATH = "/Users/nrojiani/IdeaProjects/drone-delivery-challenge/src/main/resources/input/test-input-1"

@JvmField
val ORDER_1 = Order(
    orderId = "WM001",
    destination = Coordinate(x = -5.0, y = 11.0),
    orderPlacedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 11, 50)),
    transitTime = TransitTime(transitTimeToDestination = 724L)
)

@JvmField
val ORDER_2 = Order(
    orderId = "WM002",
    destination = Coordinate(x = 2.0, y = -3.0),
    orderPlacedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 11, 55)),
    transitTime = TransitTime(transitTimeToDestination = 216L)
)

@JvmField
val ORDER_3 = Order(
    orderId = "WM003",
    destination = Coordinate(x = 50.0, y = 7.0),
    orderPlacedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(6, 11, 50)),
    transitTime = TransitTime(transitTimeToDestination = 3029L)
)

@JvmField
val ORDER_4 = Order(
    orderId = "WM004",
    destination = Coordinate(x = 5.0, y = 11.0),
    orderPlacedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 31, 50)),
    transitTime = TransitTime(transitTimeToDestination = 724L)
)

@JvmField
val TODAY: LocalDate = LocalDate.now()