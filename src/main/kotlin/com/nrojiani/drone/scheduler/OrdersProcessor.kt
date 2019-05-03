package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.delivery.TransitTime
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.scheduler.calculator.TransitTimeCalculator

/**
 * Processes & adds additional information to orders.
 * TODO: support multiple launch facilities
 */
class OrdersProcessor(
    private val orders: List<Order>,
    private val transitTimeCalculator: TransitTimeCalculator,
    private val launchFacility: Coordinate
) {
    /**
     * For every order, calculate the transit time from [launchFacility] to the order's destination.
     * Construct a [PendingDeliveryOrder] composite structure containing the [TransitTime] and [Order].
     */
    fun calculateTransitTimes(): List<PendingDeliveryOrder> = orders.map { order ->
        val transitTime = transitTimeCalculator.calculateSourceToDestinationTime(
            order.destination.distanceTo(launchFacility)
        ).run(::TransitTime)

        PendingDeliveryOrder(order, transitTime)
    }
}