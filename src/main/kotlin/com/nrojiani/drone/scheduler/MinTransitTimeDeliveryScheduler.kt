package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.DEFAULT_ZONE_OFFSET
import com.nrojiani.drone.utils.Mockable
import java.time.ZonedDateTime
import java.util.ArrayDeque
import java.util.Queue

/**
 * Schedules deliveries based on distance.
 */
@Mockable
class MinTransitTimeDeliveryScheduler(
    val operatingHours: TimeInterval,
    val delegate: SchedulingDelegate
) : DeliveryScheduler {

    /**
     * Orders that are queued for scheduling.
     */
    private val queuedOrders: Queue<PendingDeliveryOrder> = ArrayDeque()

    override fun scheduleDeliveries(
        pendingOrders: List<PendingDeliveryOrder>
    ): List<DroneDelivery> {
        // TODO require startTimeMode >= first order placed.

        if (pendingOrders.isEmpty()) return emptyList()

        val scheduled: MutableList<DroneDelivery> = ArrayList()
        val sortedByTransitTime = ordersSortedByTransitTime(pendingOrders)
        val timeFirstOrderPlaced = pendingOrders.first().dateTimeOrderPlaced
        var deliveryStartTime = delegate.calculateFirstDeliveryStartTime(timeFirstOrderPlaced)

        queuedOrders.addAll(sortedByTransitTime)
        do {
            val (newlyScheduled, rollovers) = delegate.schedule(
                sortedOrders = queuedOrders,
                startTime = deliveryStartTime
            )

            scheduled.addAll(newlyScheduled)
            queuedOrders.clear()
            queuedOrders.addAll(rollovers)
            deliveryStartTime = ZonedDateTime.of(
                deliveryStartTime.toLocalDate().plusDays(1),
                operatingHours.start.toLocalTime(),
                DEFAULT_ZONE_OFFSET
            )
        } while (queuedOrders.isNotEmpty())

        return scheduled
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
}