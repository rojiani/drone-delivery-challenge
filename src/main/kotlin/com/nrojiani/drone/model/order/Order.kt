package com.nrojiani.drone.model.order

import com.nrojiani.drone.model.Coordinate
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Basic data about an order.
 * @see [PendingDeliveryOrder]
 */
data class Order(
    val orderId: String,
    val destination: Coordinate,
    val orderPlacedDateTime: ZonedDateTime
) {
    val dateOrderPlaced: LocalDate = orderPlacedDateTime.toLocalDate()
}