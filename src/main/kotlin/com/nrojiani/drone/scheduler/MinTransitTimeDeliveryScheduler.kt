package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.model.delivery.DroneDelivery
import java.time.LocalDateTime

class MinTransitTimeDeliveryScheduler : DeliveryScheduler {
    override fun scheduleDeliveries(orders: List<Order>): List<DroneDelivery> {
        if (orders.isEmpty()) return emptyList()

        require(orders.all { it.transitTime != null }) { "Orders must have transit times calculated" }

        val sortedByTransitTime = ordersSortedByTransitTime(orders)

        val date = orders.first().orderPlacedDateTime.toLocalDate()

        return schedule(
            sortedByTransitTime, LocalDateTime.of(date, DRONE_DELIVERY_OPERATING_HOURS.start)
        )
    }

    /**
     * Sort by transit time. If 2 orders have the same transit time, take the lower orderId number.
     */
    internal fun ordersSortedByTransitTime(orders: List<Order>): List<Order> {
        val comparator: Comparator<Order> = compareBy { order: Order ->
            order.transitTime?.sourceToDestinationTime
        }.thenBy { order: Order ->
            order.orderId.drop(2).toInt()
        }

        return orders.sortedWith(comparator)
    }

    private fun schedule(sortedOrders: List<Order>, startTime: LocalDateTime): List<DroneDelivery> {
        var time: LocalDateTime = startTime
        return sortedOrders.fold(arrayListOf()) { acc, order ->
            requireNotNull(order.transitTime == null)

            val delivery =
                DroneDelivery(order, timeOrderDelivered = time.plusSeconds(order.transitTime!!.sourceToDestinationTime))
            time = delivery.timeDroneReturned

            acc.apply { acc.add(delivery) }
        }
    }

}