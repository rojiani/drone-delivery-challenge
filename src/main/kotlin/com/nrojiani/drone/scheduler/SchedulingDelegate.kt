package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.DEFAULT_ZONE_OFFSET
import java.time.ZonedDateTime

/**
 * Handles delivery calculations agnostic to the scheduling strategy.
 */
class SchedulingDelegate(
    val operatingHours: TimeInterval
) {
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
            ZonedDateTime.of(time.toLocalDate(), operatingHours.endExclusive.toLocalTime(), DEFAULT_ZONE_OFFSET)
        val scheduled: MutableList<DroneDelivery> = ArrayList()

        sortedOrders.forEachIndexed { i, order ->
            // if orderPlacedTime is after scheduling time, move scheduling time forward
            if (order.dateTimeOrderPlaced > time) {
                time = order.dateTimeOrderPlaced
            }

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