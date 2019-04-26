package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.testutils.ORDER_1
import com.nrojiani.drone.testutils.ORDER_2
import com.nrojiani.drone.testutils.ORDER_3
import com.nrojiani.drone.testutils.ORDER_4
import com.nrojiani.drone.testutils.TODAY
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MinTransitTimeDroneDeliverySchedulerTest {

    private val droneDeliveryScheduler = MinTransitTimeDeliveryScheduler()
    private val orders = listOf(ORDER_1, ORDER_2, ORDER_3, ORDER_4)

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
        val scheduled = droneDeliveryScheduler.scheduleDeliveries(orders)

        // Deliveries sorted by closest to furthest:
        assertEquals(listOf("WM002", "WM001", "WM004", "WM003"), scheduled.map { it.order.orderId })

        // Expected delivery times
        assertEquals(
            unitTestDeliveryTimes,
            scheduled.map { it.timeOrderDelivered.toLocalTime() }
        )
    }

    @Test
    fun `scheduleDeliveries - orders without transit time`() {
        val input = orders + Order("WM005", Coordinate(1.0, 4.0), LocalDateTime.of(TODAY, LocalTime.of(7, 0, 45)))

        assertFailsWith<IllegalArgumentException> {
            droneDeliveryScheduler.scheduleDeliveries(input)
        }
    }

    @Test
    fun ordersSortedByTransitTime() {
        val expected = listOf(ORDER_2, ORDER_1, ORDER_4, ORDER_3)
        assertEquals(expected, droneDeliveryScheduler.ordersSortedByTransitTime(orders))
    }
}