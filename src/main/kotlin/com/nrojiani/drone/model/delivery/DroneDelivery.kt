package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.order.PendingDeliveryOrder
import java.time.LocalDateTime

/**
 * Represents the details of a Drone delivery.
 */
data class DroneDelivery(
    val orderWithTransitTime: PendingDeliveryOrder,
    val timeOrderDelivered: LocalDateTime
) {
    private val oneWayTransitTime: Long = orderWithTransitTime.transitTime.sourceToDestinationTime

    val timeOrderPlaced: LocalDateTime = orderWithTransitTime.order.orderPlacedDateTime
    val timeDroneDeparted: LocalDateTime = timeOrderDelivered.minusSeconds(oneWayTransitTime)
    val timeDroneReturned: LocalDateTime = timeOrderDelivered.plusSeconds(oneWayTransitTime)
}