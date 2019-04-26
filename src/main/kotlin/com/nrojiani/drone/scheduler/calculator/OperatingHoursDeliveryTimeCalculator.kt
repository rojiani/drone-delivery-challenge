package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.utils.extensions.isInShortTimeInterval
import com.nrojiani.drone.utils.extensions.isSameDayAs
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.delivery.ShortTimeInterval
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Calculates the delivery time, not counting hours outside of drone delivery.
 * See Assumptions 4 & 5 in README for details.
 */
class OperatingHoursDeliveryTimeCalculator(private val operatingHours: ShortTimeInterval) : DeliveryTimeCalculator {

    override fun calculate(droneDelivery: DroneDelivery): Long {
        val dateTimeOrderPlaced: LocalDateTime = droneDelivery.timeOrderPlaced
        val dateTimeOrderDelivered: LocalDateTime = droneDelivery.timeOrderDelivered
        val (dateOrderPlaced, timeOrderPlaced) =
            dateTimeOrderPlaced.toLocalDate() to dateTimeOrderPlaced.toLocalTime()
        val (dateOrderDelivered, timeOrderDelivered) =
            dateTimeOrderDelivered.toLocalDate() to dateTimeOrderDelivered.toLocalTime()
        val duration = Duration.between(timeOrderPlaced, timeOrderDelivered)

        val wasDeliveredSameDay = dateTimeOrderPlaced.isSameDayAs(dateTimeOrderDelivered)

        val orderPlacedDuringOperatingHours = timeOrderPlaced.isInShortTimeInterval(operatingHours)

        val totalSeconds = dateTimeOrderPlaced.until(dateTimeOrderDelivered, ChronoUnit.SECONDS)

        val offHours = ShortTimeInterval(operatingHours.end, operatingHours.start)

        return when {
            orderPlacedDuringOperatingHours && wasDeliveredSameDay -> duration.seconds
            orderPlacedDuringOperatingHours && !wasDeliveredSameDay -> {
                val days = dateOrderPlaced.until(dateOrderDelivered, ChronoUnit.DAYS)
                totalSeconds - (days * offHours.seconds)
            }
            !orderPlacedDuringOperatingHours && wasDeliveredSameDay -> {
                val secondsInoperable = ShortTimeInterval(timeOrderPlaced, operatingHours.start).seconds
                // println("secondsInoperable = $secondsInoperable")
                totalSeconds - secondsInoperable
            }
            else -> {
                val secondsInoperableDay1 = ShortTimeInterval(
                    timeOrderPlaced,
                    operatingHours.start
                ).seconds
                val days = dateOrderPlaced.until(dateOrderDelivered, ChronoUnit.DAYS)
                totalSeconds - secondsInoperableDay1 - ((days - 1) * offHours.seconds)
            }
        }
    }
}