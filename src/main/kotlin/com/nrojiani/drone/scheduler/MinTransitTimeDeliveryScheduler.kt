package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.DEFAULT_CLOCK
import com.nrojiani.drone.utils.Mockable
import com.nrojiani.drone.utils.extensions.zoneOffset
import java.time.Clock
import java.time.ZonedDateTime

/**
 * Schedules deliveries based on distance.
 */
@Mockable
class MinTransitTimeDeliveryScheduler(
    val operatingHours: TimeInterval,
    private val clock: Clock = DEFAULT_CLOCK
) : DeliveryScheduler {

    /**
     * Orders that must be rescheduled because their delivery can't be completed within operating hours.
     * Orders are sorted by transit time.
     */
    private var rolloverOrders: List<PendingDeliveryOrder> = ArrayList()

    /**
     * Get the current time using the [clock] specified when creating an instance of this class.
     * This exists solely for the purpose of mocking the current time.
     */
    internal val currentZonedDateTime = ZonedDateTime.now(clock)

    override fun scheduleDeliveries(pendingOrders: List<PendingDeliveryOrder>): List<DroneDelivery> {
        if (pendingOrders.isEmpty()) return emptyList()

        val scheduled: MutableList<DroneDelivery> = ArrayList()
        val sortedByTransitTime = ordersSortedByTransitTime(pendingOrders)
        val earliestDeliveryStartTime = calculateFirstDeliveryStartTime()

        do {
            val (newlyScheduled, rollovers) = schedule(
                sortedOrders = sortedByTransitTime,
                startTime = ZonedDateTime.of(
                    earliestDeliveryStartTime.toLocalDate(),
                    DRONE_DELIVERY_OPERATING_HOURS.start.toLocalTime(),
                    clock.zone
                )
            )

            scheduled.addAll(newlyScheduled)
            rolloverOrders = rollovers
        } while (rolloverOrders.isNotEmpty())

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
        sortedOrders: List<PendingDeliveryOrder>,
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
     * TODO
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