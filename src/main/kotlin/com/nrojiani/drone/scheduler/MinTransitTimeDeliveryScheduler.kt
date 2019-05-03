package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.DEFAULT_CLOCK
import com.nrojiani.drone.utils.Mockable
import com.nrojiani.drone.utils.extensions.zoneOffset
import java.time.Clock
import java.time.ZonedDateTime
import java.util.ArrayDeque
import java.util.Queue

/**
 * Schedules deliveries based on distance.
 */
@Mockable
class MinTransitTimeDeliveryScheduler(
    val operatingHours: TimeInterval,
    private val clock: Clock = DEFAULT_CLOCK
) : DeliveryScheduler {

    /**
     * Orders that are queued for scheduling.
     */
    private val queuedOrders: Queue<PendingDeliveryOrder> = ArrayDeque()

    /**
     * Get the current time using the [clock] specified when creating an instance of this class.
     * This exists solely for the purpose of mocking the current time.
     */
    internal val currentZonedDateTime = ZonedDateTime.now(clock)

    override fun scheduleDeliveries(
        pendingOrders: List<PendingDeliveryOrder>,
        startTime: ZonedDateTime?
    ): List<DroneDelivery> {

        // TODO require startTime >= first order placed.

        if (pendingOrders.isEmpty()) return emptyList()

        val scheduled: MutableList<DroneDelivery> = ArrayList()
        val sortedByTransitTime = ordersSortedByTransitTime(pendingOrders)
        var deliveryStartTime = startTime ?: calculateFirstDeliveryStartTime()


        queuedOrders.addAll(sortedByTransitTime)
        do {
            val (newlyScheduled, rollovers) = schedule(
                sortedOrders = queuedOrders,
                startTime = ZonedDateTime.of(
                    deliveryStartTime.toLocalDate(),
                    deliveryStartTime.toLocalTime(),
                    clock.zone
                )
            )

            scheduled.addAll(newlyScheduled)
            queuedOrders.clear()
            queuedOrders.addAll(rollovers)
            deliveryStartTime = ZonedDateTime.of(
                deliveryStartTime.toLocalDate().plusDays(1),
                operatingHours.start.toLocalTime(),
                clock.zoneOffset
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

        // TODO thenBy orderPlacedDateTime

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
            ZonedDateTime.of(startTime.toLocalDate(), operatingHours.endExclusive.toLocalTime(), clock.zone)
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

    /**
     * Use the current time (calculated using the specified [clock]) to calculate when the
     * first delivery could be made - returns either the current time if during operating hours,
     * or the start of the next delivery period (on the current or following day).
     */
    internal fun calculateFirstDeliveryStartTime(): ZonedDateTime {
        val currentTime: ZonedDateTime = currentZonedDateTime

        // Today's delivery hours
        val deliveryHours = operatingHours.toZonedDateTimeInterval(
            currentTime.toLocalDate(), currentTime.toLocalDate()
        )

        return when {
            currentTime < deliveryHours.start -> deliveryHours.start
            currentTime in deliveryHours -> currentTime
            else -> ZonedDateTime.of(
                currentTime.toLocalDate().plusDays(1),
                operatingHours.start.toLocalTime(),
                clock.zoneOffset
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