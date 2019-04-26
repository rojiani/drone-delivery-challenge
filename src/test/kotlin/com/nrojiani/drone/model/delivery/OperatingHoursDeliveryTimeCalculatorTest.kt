package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.scheduler.DeliveryTimeCalculator
import com.nrojiani.drone.scheduler.OperatingHoursDeliveryTimeCalculator
import com.nrojiani.drone.testutils.ORDER_4
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertEquals

class OperatingHoursDeliveryTimeCalculatorTest {

    private val operatingHours = DRONE_DELIVERY_OPERATING_HOURS
    private val calculator: DeliveryTimeCalculator =
        OperatingHoursDeliveryTimeCalculator(operatingHours)

    // 06:11:50 -> 07:15:45 = 01:03:55
    private val sameDayDeliveryPlacedDuringOpHours = DroneDelivery(
        order = ORDER_4,
        timeOrderDelivered = LocalDateTime.of(ORDER_4.orderPlacedDateTime.toLocalDate(), LocalTime.of(7, 15, 45))
    )

    // 06:11:50 on day 1 -> 07:15:45 on day 2 = 01:01:03:55
    private val nextDayDeliveryPlacedDuringOpHours = DroneDelivery(
        order = ORDER_4,
        timeOrderDelivered = LocalDateTime.of(
            ORDER_4.orderPlacedDateTime.toLocalDate().plusDays(1),
            LocalTime.of(7, 15, 45)
        )
    )


    @Test
    fun `calculateDeliveryTime - placed during operating hours and delivered same day`() {
        assertEquals(
            hoursToSeconds(1) + minsToSeconds(3) + 55L,
            calculator.calculate(sameDayDeliveryPlacedDuringOpHours)
        )
    }

    @Test
    fun `calculateDeliveryTime - order placed during operating hours and delivered next day`() {
        assertEquals(
            90235L - 28800L,
            calculator.calculate(nextDayDeliveryPlacedDuringOpHours)
        )
    }

    // TODO complete testing
}