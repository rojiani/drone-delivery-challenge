package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import java.time.LocalDateTime

/**
 * Schedules deliveries based on distance.
 */
class MinTransitTimeDeliveryScheduler : DeliveryScheduler {
    override fun scheduleDeliveries(orders: List<PendingDeliveryOrder>): List<DroneDelivery> {
        if (orders.isEmpty()) return emptyList()

        val sortedByTransitTime = ordersSortedByTransitTime(orders)
        val date = orders.first().dateOrderPlaced
        return schedule(
            sortedByTransitTime, LocalDateTime.of(date, DRONE_DELIVERY_OPERATING_HOURS.start)
        )
    }

    /**
     * Sort by transit time. If 2 orders have the same transit time, take the lower orderId number
     * (e.g., WM006 before WM007).
     */
    internal fun ordersSortedByTransitTime(pendingOrders: List<PendingDeliveryOrder>): List<PendingDeliveryOrder> {
        val comparator = compareBy { pendingDeliveryOrder: PendingDeliveryOrder ->
            pendingDeliveryOrder.transitTime.sourceToDestinationTime
        }.thenBy { pendingDeliveryOrder: PendingDeliveryOrder ->
            pendingDeliveryOrder.orderId.drop(2).toInt()
        }

        return pendingOrders.sortedWith(comparator)
    }

    /**
     * Generate list of deliveries with times generated from the order's transit times.
     * @param sortedOrders orders sorted by distance (increasing)
     * @param startTime the time the first delivery will depart
     */
    private fun schedule(sortedOrders: List<PendingDeliveryOrder>, startTime: LocalDateTime): List<DroneDelivery> {

        // TODO - postpone deliveries after 10pm

        var time: LocalDateTime = startTime
        return sortedOrders.fold(arrayListOf()) { acc, pendingOrder ->
            val delivery = DroneDelivery(
                pendingOrder,
                timeOrderDelivered = time.plusSeconds(pendingOrder.transitTime.sourceToDestinationTime)
            )
            time = delivery.timeDroneReturned

            acc.apply { acc.add(delivery) }
        }
    }
}