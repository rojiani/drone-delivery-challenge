package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator
import com.nrojiani.drone.testutils.ORDER_4
import com.nrojiani.drone.testutils.PENDING_ORDER_4
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.utils.hoursToSeconds
import com.nrojiani.drone.utils.minsToSeconds
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertEquals

class OperatingHoursDeliveryTimeCalculatorTest {

    private val operatingHours = DRONE_DELIVERY_OPERATING_HOURS
    private val dtCalculator: DeliveryTimeCalculator =
        OperatingHoursDeliveryTimeCalculator(operatingHours)

    @Test
    fun `calculateDeliveryTime - placed during operating hours and delivered same day`() {
        // 06:11:50 -> 07:15:45 = 01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = LocalDateTime.of(ORDER_4.dateOrderPlaced, LocalTime.of(7, 15, 45))
        )

        assertEquals(
            hoursToSeconds(1) + minsToSeconds(3) + 55L,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed during operating hours and delivered next day`() {
        // 06:11:50 on day 1 -> 07:15:45 on day 2 = 01:01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = LocalDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(1),
                LocalTime.of(7, 15, 45)
            )
        )

        assertEquals(
            90235L - 28800L,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed during operating hours and delivered multiple days later`() {
        // 06:11:50 on day 1 -> 07:15:45 on day 4 = 03:01:03:55
        val delivery = DroneDelivery(
            orderWithTransitTime = PENDING_ORDER_4,
            timeOrderDelivered = LocalDateTime.of(
                ORDER_4.dateOrderPlaced.plusDays(3),
                LocalTime.of(7, 15, 45)
            )
        )
        // Note: Expected value is calculated differently than in method under test (not just repeating logic)
        val day1 = 56890L                                   // 06:11:50 -> 22:00:00 = 15:48:10
        val day2and3 = hoursToSeconds(16) * 2L              // 57600 * 2 = 115,200 s
        val day4 = 4545L                                    // 06:00:00 -> 07:15:45 = 01:15:45
        val expectedDeliveryTime = day1 + day2and3 + day4   // 176,635 s
        assertEquals(
            expectedDeliveryTime,
            dtCalculator.calculate(delivery)
        )
    }

    @Test
    fun `calculateDeliveryTime - placed during off-hours and delivered same day`() {
        // placed -> delivered    02:15:00 -> 08:00:00 = 05:45:00.
        // open -> delivered      06:00:00 -> 08:00:00 = 02:00:00
        val delivery = DroneDelivery(
            orderWithTransitTime = PendingDeliveryOrder(
                orderId = "WM005",
                destination = Coordinate(x = 1.0, y = 1.0),
                orderPlacedDateTime = LocalDateTime.of(TODAY, LocalTime.parse("02:15:00")),
                transitTime = TransitTime(hoursToSeconds(2))
            ),
            timeOrderDelivered = LocalDateTime.of(ORDER_4.dateOrderPlaced, LocalTime.parse("08:00:00"))
        )
        assertEquals(hoursToSeconds(2), dtCalculator.calculate(delivery))
    }

//    @Test
//    fun `calculateDeliveryTime - placed during off-hours and delivered next day`() {
//        assertEquals(
//            90235L - 28800L,
//            dtCalculator.calculate(nextDayDeliveryPlacedDuringOpHours)
//        )
//    }
//
//    @Test
//    fun `calculateDeliveryTime - placed during off-hours and delivered multiple days later`() {
//
//        // Note: Expected value is calculated differently than in method under test (not just repeating logic)
//        val day1 = 56890L                                   // 06:11:50 -> 22:00:00 = 15:48:10
//        val day2and3 = hoursToSeconds(16) * 2L              // 57600 * 2 = 115,200 s
//        val day4 = 4545L                                    // 06:00:00 -> 07:15:45 = 01:15:45
//        val expectedDeliveryTime = day1 + day2and3 + day4   // 176,635 s
//        assertEquals(
//            expectedDeliveryTime,
//            dtCalculator.calculate(multiDayDeliveryPlacedDuringOpHours)
//        )
//    }

}