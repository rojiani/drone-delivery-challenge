package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.order.PendingDeliveryOrder
import java.time.ZonedDateTime

/**
 * Represents the details of a Drone delivery.
 */
data class DroneDelivery(
    val orderWithTransitTime: PendingDeliveryOrder,
    val timeOrderDelivered: ZonedDateTime
) {
    private val oneWayTransitTime: Long = orderWithTransitTime.transitTime.sourceToDestinationTime

    val timeOrderPlaced: ZonedDateTime = orderWithTransitTime.order.orderPlacedDateTime
    val timeDroneDeparted: ZonedDateTime = timeOrderDelivered.minusSeconds(oneWayTransitTime)
    val timeDroneReturned: ZonedDateTime = timeOrderDelivered.plusSeconds(oneWayTransitTime)
}