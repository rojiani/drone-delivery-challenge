package com.nrojiani.drone.model.order

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.delivery.TransitTime
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * An [Order] with a calculated [TransitTime].
 */
data class PendingDeliveryOrder(val order: Order, val transitTime: TransitTime) {

    val orderId: String = order.orderId
    val dateOrderPlaced: LocalDate = order.orderPlacedDateTime.toLocalDate()
    val dateTimeOrderPlaced: ZonedDateTime = order.orderPlacedDateTime

    /** Secondary constructor */
    constructor(
        orderId: String,
        destination: Coordinate,
        orderPlacedDateTime: ZonedDateTime,
        transitTime: TransitTime
    ) : this(order = Order(orderId, destination, orderPlacedDateTime), transitTime = transitTime)
}