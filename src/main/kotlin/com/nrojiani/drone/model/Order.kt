package com.nrojiani.drone.model

import com.nrojiani.drone.model.delivery.TransitTime
import java.time.LocalDateTime

class Order(
    val orderId: String,
    val destination: Coordinate,
    val orderPlacedDateTime: LocalDateTime,
    var transitTime: TransitTime? = null
) {
    override fun toString(): String {
        return "Order(orderId='$orderId', destination=$destination, orderPlacedDateTime=$orderPlacedDateTime, transitTime=$transitTime)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        if (orderId != other.orderId) return false
        if (destination != other.destination) return false
        if (orderPlacedDateTime != other.orderPlacedDateTime) return false
        if (transitTime != other.transitTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderId.hashCode()
        result = 31 * result + destination.hashCode()
        result = 31 * result + orderPlacedDateTime.hashCode()
        result = 31 * result + (transitTime?.hashCode() ?: 0)
        return result
    }
}