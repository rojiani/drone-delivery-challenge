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
class MinTransitTimeDeliveryScheduler(val operatingHours: TimeInterval) : DeliveryScheduler {

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
        var timeFirstOrderPlaced = pendingOrders.first().dateTimeOrderPlaced
        var deliveryStartTime = calculateFirstDeliveryStartTime(timeFirstOrderPlaced)

        // TODO
        println("START TIME: $deliveryStartTime")

        queuedOrders.addAll(sortedByTransitTime)
        do {
            val (newlyScheduled, rollovers) = schedule(
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

    /**
     * Process the sortedOrders one time. Generates list of deliveries with times generated from the order's transit
     * times. Also returns the orders that can't be delivered and return to the launch facility by the end of operating
     * hours.
     *
     * @param sortedOrders orders sorted by distance (increasing)
     * @param startTime the time the first delivery will depart
     * @return A [SchedulingResult] consisting of the scheduled and unscheduled rollover orders
     */
    internal fun schedule(
        sortedOrders: Iterable<PendingDeliveryOrder>,
        startTime: ZonedDateTime
    ): SchedulingResult {
        var time: ZonedDateTime = startTime

        val closingTime =
            ZonedDateTime.of(startTime.toLocalDate(), operatingHours.endExclusive.toLocalTime(), DEFAULT_ZONE_OFFSET)
        val scheduled: MutableList<DroneDelivery> = ArrayList()

        sortedOrders.forEachIndexed { i, order ->
            val delivery = DroneDelivery(
                order,
                timeOrderDelivered = time.plusSeconds(order.transitTime.sourceToDestinationTime)
            )

            // Package must not only be delivered to customer, but also return to launch facility
            // before end of operating hours.
            if (delivery.timeDroneReturned <= closingTime) {
                scheduled += delivery
                time = delivery.timeDroneReturned
            } else {
                val rollover: List<PendingDeliveryOrder> = sortedOrders.drop(i)
                return SchedulingResult(scheduled, rollover)
            }
        }

        return SchedulingResult(scheduled, emptyList())
    }

    internal fun calculateFirstDeliveryStartTime(
        timeFirstOrderPlaced: ZonedDateTime
    ): ZonedDateTime {
        // Today's delivery hours
        val deliveryHours = operatingHours.toZonedDateTimeInterval(
            timeFirstOrderPlaced.toLocalDate(), timeFirstOrderPlaced.toLocalDate()
        )
        println("deliveryHours: $deliveryHours")
        println("timeFirstOrderPlaced: $timeFirstOrderPlaced")


        return when {
            timeFirstOrderPlaced < deliveryHours.start -> deliveryHours.start
            timeFirstOrderPlaced in deliveryHours -> timeFirstOrderPlaced
            else -> ZonedDateTime.of(
                timeFirstOrderPlaced.toLocalDate().plusDays(1),
                operatingHours.start.toLocalTime(),
                DEFAULT_ZONE_OFFSET
            )
        }
    }
}

/**
 * Wrapper class that simply holds the result of a single scheduling operation.
 * @param scheduled A list of [DroneDelivery] generated by scheduling a list of [PendingDeliveryOrder].
 * @param unscheduled The orders that could not be scheduled. Possibly empty.
 */
internal data class SchedulingResult(
    val scheduled: List<DroneDelivery>,
    val unscheduled: List<PendingDeliveryOrder>
)