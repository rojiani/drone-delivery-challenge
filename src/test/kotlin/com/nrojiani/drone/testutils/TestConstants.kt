package com.nrojiani.drone.testutils

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.delivery.TransitTime
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.utils.UTC_ZONE_ID
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

/** Maximum tolerated difference when comparing floating-point types for equality. */
const val EPSILON = 1e-10

/** Absolute filepath to sample test input. */
const val TEST_INPUT_FILEPATH =
    "/Users/nrojiani/IdeaProjects/drone-delivery-challenge/src/main/resources/input/test-input-1"

/** The ORIGIN on the coordinate system. */
@JvmField
val ORIGIN = Coordinate(0, 0)

@JvmField
val TODAY: LocalDate = LocalDate.now()

object OrderData {

    @JvmField
    val ORDER_1 = Order(
        orderId = "WM001",
        destination = Coordinate(x = -5.0, y = 11.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.of(5, 11, 50), UTC_ZONE_ID)
    )

    @JvmField
    val ORDER_2 = Order(
        orderId = "WM002",
        destination = Coordinate(x = 2.0, y = -3.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.of(5, 11, 55), UTC_ZONE_ID)
    )

    @JvmField
    val ORDER_3 = Order(
        orderId = "WM003",
        destination = Coordinate(x = 50.0, y = 7.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.of(5, 31, 50), UTC_ZONE_ID)
    )

    @JvmField
    val ORDER_4 = Order(
        orderId = "WM004",
        destination = Coordinate(x = 5.0, y = 11.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.of(6, 11, 50), UTC_ZONE_ID)
    )

    @JvmField
    val PENDING_ORDER_1 = PendingDeliveryOrder(ORDER_1, TransitTime(725L))
    @JvmField
    val PENDING_ORDER_2 = PendingDeliveryOrder(ORDER_2, TransitTime(216L))
    @JvmField
    val PENDING_ORDER_3 = PendingDeliveryOrder(ORDER_3, TransitTime(3029L))
    @JvmField
    val PENDING_ORDER_4 = PendingDeliveryOrder(ORDER_4, TransitTime(725L))

    @JvmField
    val ORDERS: List<Order> = listOf(ORDER_1, ORDER_2, ORDER_3, ORDER_4)

    @JvmField
    val ORDERS_WITH_TRANSIT_TIMES: List<PendingDeliveryOrder> =
        listOf(PENDING_ORDER_1, PENDING_ORDER_2, PENDING_ORDER_3, PENDING_ORDER_4)

    @JvmField
    val ORDERS_SORTED_BY_TRANSIT_TIMES: List<PendingDeliveryOrder> =
        listOf(PENDING_ORDER_2, PENDING_ORDER_1, PENDING_ORDER_4, PENDING_ORDER_3)
}
