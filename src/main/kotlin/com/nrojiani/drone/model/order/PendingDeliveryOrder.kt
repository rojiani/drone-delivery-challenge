package com.nrojiani.drone.model.order

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.delivery.TransitTime
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * An [Order] with a calculated [TransitTime].
 */
data class PendingDeliveryOrder(val order: Order, val transitTime: TransitTime) {

    val orderId: String = order.orderId
    val dateOrderPlaced: LocalDate = order.orderPlacedDateTime.toLocalDate()

    /** Secondary constructor */
    constructor(
        orderId: String,
        destination: Coordinate,
        orderPlacedDateTime: LocalDateTime,
        transitTime: TransitTime
    ) : this(order = Order(orderId, destination, orderPlacedDateTime), transitTime = transitTime)
}