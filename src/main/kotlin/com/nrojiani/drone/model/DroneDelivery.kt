package com.nrojiani.drone.model

import java.time.LocalDateTime

/**
 * Represents the details of a Drone delivery.
 */
data class DroneDelivery(
    val order: Order,
    val timeOrderDelivered: LocalDateTime
) {
    val timeOrderPlaced: LocalDateTime = order.orderPlacedDateTime
    val timeDroneReturned: LocalDateTime
        get() {
            // non-null validated in initializer block
            val oneWayTime = order.transitTime!!.sourceToDestinationTime
            return timeOrderDelivered.plusSeconds(oneWayTime)
        }

    init {
        require(order.transitTime != null) {
            "order has no transitTime"
        }
    }
}