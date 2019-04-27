package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator.OrderPlacementTime.AFTER
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator.OrderPlacementTime.BEFORE
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator.OrderPlacementTime.DURING
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.extensions.dateAndTime
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Calculates the delivery time, not counting hours outside of drone delivery.
 * See Assumptions 4 & 5 in README for details.
 */
class OperatingHoursDeliveryTimeCalculator(private val operatingHours: TimeInterval) : DeliveryTimeCalculator {

    /** Time order was placed relative to the [operatingHours] */
    private enum class OrderPlacementTime { BEFORE, DURING, AFTER }

    override fun calculate(droneDelivery: DroneDelivery): Long {
        val (dateOrderPlaced, timeOrderPlaced) = droneDelivery.timeOrderPlaced.dateAndTime
        val (dateOrderDelivered, timeOrderDelivered) = droneDelivery.timeOrderDelivered.dateAndTime

        val relativeOrderPlacement: OrderPlacementTime = when {
            timeOrderPlaced < operatingHours.start -> BEFORE
            timeOrderPlaced in operatingHours -> DURING
            else -> AFTER
        }

        return when (relativeOrderPlacement) {
            BEFORE -> {
                val days = dateOrderPlaced.until(dateOrderDelivered, ChronoUnit.DAYS)
                (days * operatingHours.seconds) +
                        Duration.between(operatingHours.start, timeOrderDelivered).seconds
            }
            DURING -> {
                val firstDay = Duration.between(timeOrderPlaced, operatingHours.endExclusive).seconds
                val lastDay = Duration.between(operatingHours.start, timeOrderDelivered).seconds
                val days = dateOrderPlaced.until(dateOrderDelivered, ChronoUnit.DAYS)
                firstDay + ((days - 1) * operatingHours.seconds) + lastDay
            }
            AFTER -> {
                val days = dateOrderPlaced.until(dateOrderDelivered, ChronoUnit.DAYS)
                Duration.between(operatingHours.start, timeOrderDelivered).seconds +
                        ((days - 1) * operatingHours.seconds)
            }
        }
    }
}