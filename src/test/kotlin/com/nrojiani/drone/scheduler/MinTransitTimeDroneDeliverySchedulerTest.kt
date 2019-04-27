package com.nrojiani.drone.scheduler

import com.nrojiani.drone.testutils.OrderData.ORDERS_WITH_TRANSIT_TIMES
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_2
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_3
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_4
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertEquals

class MinTransitTimeDroneDeliverySchedulerTest {

    private val droneDeliveryScheduler = MinTransitTimeDeliveryScheduler()

    // TODO investigate discrepancy b/t appDeliveryTimes & unitTestDeliveryTimes
    // When running app
    private val appDeliveryTimes = listOf(
        LocalTime.parse("06:03:36"),
        LocalTime.parse("06:19:17"),
        LocalTime.parse("06:43:27"),
        LocalTime.parse("07:46:01")
    )

    // When running unit test. Unclear what the cause of discrepancy is... TODO
    private val unitTestDeliveryTimes = listOf(
        LocalTime.parse("06:03:36"),
        LocalTime.parse("06:19:16"),
        LocalTime.parse("06:43:24"),
        LocalTime.parse("07:45:57")
    )

    @Test
    fun scheduleDeliveries() {
        val scheduled = droneDeliveryScheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries sorted by closest to furthest:
        assertEquals(listOf("WM002", "WM001", "WM004", "WM003"), scheduled.map { it.orderWithTransitTime.orderId })

        // Expected delivery times
        assertEquals(
            unitTestDeliveryTimes,
            scheduled.map { it.timeOrderDelivered.toLocalTime() }
        )
    }

    @Test
    fun ordersSortedByTransitTime() {
        val expected = listOf(PENDING_ORDER_2, PENDING_ORDER_1, PENDING_ORDER_4, PENDING_ORDER_3)
        assertEquals(expected, droneDeliveryScheduler.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES))
    }
}