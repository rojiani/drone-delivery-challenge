package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.time.UTC_ZONE_ID
import com.nrojiani.drone.testutils.OrderData.ORDERS_SORTED_BY_TRANSIT_TIMES
import com.nrojiani.drone.testutils.OrderData.ORDERS_WITH_TRANSIT_TIMES
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_2
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_3
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_4
import com.nrojiani.drone.testutils.TODAY
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class MinTransitTimeDroneDeliverySchedulerTest {

    private val droneDeliveryScheduler = MinTransitTimeDeliveryScheduler(
        DRONE_DELIVERY_OPERATING_HOURS,
        Clock.systemUTC()
    )

    @Test
    fun scheduleDeliveries() {
        val scheduled = droneDeliveryScheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries are sorted by closest to furthest:
        assertEquals(
            listOf("WM002", "WM001", "WM004", "WM003"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        // Expected delivery times
        val expectedDeliveryTimes = EXPECTED_SCHEDULED_TODAY.map { it.timeOrderDelivered }
        val actualDeliveryTimes = scheduled.map { it.timeOrderDelivered }
        assertEquals(expectedDeliveryTimes, actualDeliveryTimes)

        // Everything
        assertEquals(EXPECTED_SCHEDULED_TODAY, scheduled)
    }

    @Test
    fun schedule() {
        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_TODAY,
                unscheduled = emptyList()
            ),
            droneDeliveryScheduler.schedule(
                ORDERS_SORTED_BY_TRANSIT_TIMES,
                ZonedDateTime.of(TODAY, droneDeliveryScheduler.operatingHours.start, UTC_ZONE_ID)
            )
        )
    }

    @Test
    fun ordersSortedByTransitTime() {
        assertEquals(
            ORDERS_SORTED_BY_TRANSIT_TIMES,
            droneDeliveryScheduler.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES)
        )
    }

    @Test
    fun calculateFirstDeliveryStartTime() {
        val day1 = LocalDate.of(2019, 4, 25)
        val day2 = LocalDate.of(2019, 4, 26)

        val beforeOpHoursTime = LocalTime.of(4, 30, 0)
        val beforeOpHours = ZonedDateTime.of(day1, beforeOpHoursTime, UTC_ZONE_ID)
        assertEquals(
            ZonedDateTime.of(day1, droneDeliveryScheduler.operatingHours.start, UTC_ZONE_ID),
            droneDeliveryScheduler.calculateFirstDeliveryStartTime(beforeOpHours)
        )

        val duringOpHoursTime = LocalTime.of(12, 0, 0)
        val duringOpHours = ZonedDateTime.of(day1, duringOpHoursTime, UTC_ZONE_ID)
        assertEquals(
            duringOpHours,
            droneDeliveryScheduler.calculateFirstDeliveryStartTime(duringOpHours)
        )

        val afterOpHoursTime = LocalTime.of(23, 30, 0)
        val afterOpHours = ZonedDateTime.of(day1, afterOpHoursTime, UTC_ZONE_ID)
        assertEquals(
            ZonedDateTime.of(day2, droneDeliveryScheduler.operatingHours.start, UTC_ZONE_ID),
            droneDeliveryScheduler.calculateFirstDeliveryStartTime(afterOpHours)
        )
    }

    companion object {
        private val EXPECTED_SCHEDULED_TODAY = listOf(
            DroneDelivery(PENDING_ORDER_2, ZonedDateTime.of(TODAY, LocalTime.parse("06:03:36"), UTC_ZONE_ID)),
            DroneDelivery(PENDING_ORDER_1, ZonedDateTime.of(TODAY, LocalTime.parse("06:19:17"), UTC_ZONE_ID)),
            DroneDelivery(PENDING_ORDER_4, ZonedDateTime.of(TODAY, LocalTime.parse("06:43:27"), UTC_ZONE_ID)),
            DroneDelivery(PENDING_ORDER_3, ZonedDateTime.of(TODAY, LocalTime.parse("07:46:01"), UTC_ZONE_ID))
        )

//        private val EXPECTED_SCHEDULED_ORDER_DATE = EXPECTED_SCHEDULED_TODAY.map { delivery ->
//            DroneDelivery(
//                delivery.orderWithTransitTime,
//                ZonedDateTime.of(DATE_ORDER_PLACED, delivery.timeOrderDelivered.toLocalTime(), UTC_ZONE_ID)
//            )
//        }
    }
}