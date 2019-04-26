package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.Order

class MinTransitTimeDeliveryScheduler : DeliveryScheduler {
    override fun scheduleDeliveries(orders: List<Order>) {
        TODO()
    }

    /**
     * Sort by transit time. If 2 orders have the same transit time, take the lower orderId number.
     */
    fun ordersSortedByTransitTime(orders: List<Order>): List<Order> {
        val comparator: Comparator<Order> = compareBy { order: Order ->
            order.transitTime?.sourceToDestinationTime
        }.thenBy { order: Order ->
            order.orderId.drop(2).toInt()
        }

        return orders.sortedWith(comparator)
    }
}