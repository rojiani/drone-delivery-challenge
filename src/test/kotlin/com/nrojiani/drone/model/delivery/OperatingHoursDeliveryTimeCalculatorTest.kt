package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator
import com.nrojiani.drone.testutils.OrderData.ORDER_4
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_4
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.utils.UTC_ZONE_ID
import com.nrojiani.drone.utils.hoursToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Test
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class OperatingHoursDeliveryTimeCalculatorTest {

    private val operatingHours = DRONE_DELIVERY_OPERATING_HOURS
    private val dtCalculator: DeliveryTimeCalculator =
        OperatingHoursDeliveryTimeCalculator(operatingHours)

    private val orderBeforeOp = PendingDeliveryOrder(
        orderId = "WM005",
        destination = Coordinate(x = 1.0, y = 1.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.parse("02:15:00"), UTC_ZONE_ID),
        transitTime = TransitTime(hoursToSeconds(2))
    )

    private val orderAfterOp = PendingDeliveryOrder(
        orderId = "WM006",
        destination = Coordinate(x = 1.0, y = 1.0),
        orderPlacedDateTime = ZonedDateTime.of(TODAY, LocalTime.parse("23:00:00"), UTC_ZONE_ID),
        transitTime = TransitTime(hoursToSeconds(2))
    )

    @Test
    fun `calculateDeliveryTime - placed during operating hours & delivered same day`() {
        // 06:11:50 -> 07:15:45 = 01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = ZonedDateTime.of(ORDER_4.dateOrderPlaced, LocalTime.of(7, 15, 45), UTC_ZONE_ID)
        )

        assertEquals(
            hoursToSeconds(1) + minsToSeconds(3) + 55L,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed during operating hours & delivered next day`() {
        // 06:11:50 on day 1 -> 07:15:45 on day 2 = 01:01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(1),
                LocalTime.of(7, 15, 45),
                UTC_ZONE_ID
            )
        )

        assertEquals(
            90235L - 28800L,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed during operating hours & delivered multiple days later`() {
        // 06:11:50 on day 1 -> 07:15:45 on day 4 = 03:01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(3),
                LocalTime.of(7, 15, 45),
                UTC_ZONE_ID
            )
        )
        // 06:11:50 -> 22:00:00 = 15:48:10
        val day1 = 56890L
        // 57600 * 2 = 115,200 s
        val day2and3 = hoursToSeconds(16) * 2L
        // 06:00:00 -> 07:15:45 = 01:15:45
        val day4 = 4545L
        val expectedDeliveryTime = day1 + day2and3 + day4
        assertEquals(
            expectedDeliveryTime,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed before operating hours & delivered same day`() {
        // placed -> delivered    02:15:00 -> 08:00:00 = 05:45:00.
        // open -> delivered      06:00:00 -> 08:00:00 = 02:00:00 => 7,200 s
        val delivery = DroneDelivery(
            orderWithTransitTime = orderBeforeOp,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced, LocalTime.parse("08:00:00"),
                UTC_ZONE_ID
            )
        )
        assertEquals(hoursToSeconds(2), dtCalculator.calculate(delivery))
    }

    @Test
    fun `calculateDeliveryTime - placed before operating hours & delivered next day`() {
        val delivery = DroneDelivery(
            orderWithTransitTime = orderBeforeOp,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(1),
                LocalTime.parse("08:00:00"),
                UTC_ZONE_ID
            )
        )
        assertEquals(
            operatingHours.seconds + hoursToSeconds(2),
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed before operating hours & delivered multiple days later`() {
        val delivery = DroneDelivery(
            orderWithTransitTime = orderBeforeOp,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(3),
                LocalTime.parse("08:00:00"),
                UTC_ZONE_ID
            )
        )
        assertEquals(
            (operatingHours.seconds * 3) + hoursToSeconds(2),
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed after operating hours & delivered next day`() {
        val delivery = DroneDelivery(
            orderWithTransitTime = orderAfterOp,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(1),
                LocalTime.parse("08:00:00"),
                UTC_ZONE_ID
            )
        )
        assertEquals(hoursToSeconds(2), dtCalculator.calculate(delivery))
    }

    @Test
    fun `calculateDeliveryTime - placed after operating hours & delivered multiple days later`() {
        val delivery = DroneDelivery(
            orderWithTransitTime = orderAfterOp,
            timeOrderDelivered = ZonedDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(3),
                LocalTime.parse("08:00:00"),
                UTC_ZONE_ID
            )
        )
        assertEquals(
            hoursToSeconds(2) + (operatingHours.seconds * 2),
            dtCalculator.calculate(delivery)
        )
    }
}