package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.Order
import java.time.LocalDateTime

/**
 * Represents the details of a Drone delivery.
 */
data class DroneDelivery(
    val order: Order,
    val timeOrderDelivered: LocalDateTime
) {
    // Guaranteed non-null (init block validation)
    private val oneWayTransitTime: Long = order.transitTime!!.sourceToDestinationTime

    val timeOrderPlaced: LocalDateTime = order.orderPlacedDateTime
    val timeDroneDeparted: LocalDateTime = timeOrderDelivered.minusSeconds(oneWayTransitTime)
    val timeDroneReturned: LocalDateTime = timeOrderDelivered.plusSeconds(oneWayTransitTime)

    init {
        requireNotNull(order.transitTime) {
            "order has no transitTime"
        }
    }
}