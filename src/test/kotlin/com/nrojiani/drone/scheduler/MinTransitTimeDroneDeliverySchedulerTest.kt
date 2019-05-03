package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.testutils.Test1OrderData.ORDERS_SORTED_BY_TRANSIT_TIMES
import com.nrojiani.drone.testutils.Test1OrderData.ORDERS_WITH_TRANSIT_TIMES
import com.nrojiani.drone.testutils.Test2OrderData
import com.nrojiani.drone.testutils.Test2OrderData.EXPECTED_SCHEDULED_NO_ROLLOVER
import com.nrojiani.drone.testutils.Test2OrderData.EXPECTED_SCHEDULED_WITH_ROLLOVER
import org.junit.Test
import kotlin.test.assertEquals

class MinTransitTimeDroneDeliverySchedulerTest {

    private val scheduler = MinTransitTimeDeliveryScheduler(
        DRONE_DELIVERY_OPERATING_HOURS,
        SchedulingDelegate(DRONE_DELIVERY_OPERATING_HOURS)
    )

    @Test
    fun `scheduleDeliveries - all scheduled`() {
        val scheduled = scheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries are sorted by closest to furthest:
        assertEquals(
            listOf("WM002", "WM001", "WM004", "WM003"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        // Expected delivery times
        assertEquals(
            scheduled.map { it.timeOrderDelivered },
            EXPECTED_SCHEDULED_NO_ROLLOVER.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_NO_ROLLOVER, scheduled)
    }

    @Test
    fun `scheduleDeliveries - some rollovers`() {
        val scheduled = scheduler.scheduleDeliveries(Test2OrderData.ORDERS_WITH_TRANSIT_TIMES)

        assertEquals(
            listOf("WM005", "WM006", "WM007", "WM008"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        assertEquals(
            EXPECTED_SCHEDULED_WITH_ROLLOVER.map { it.timeOrderDelivered },
            scheduled.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_WITH_ROLLOVER, scheduled)
    }

    @Test
    fun ordersSortedByTransitTime() {
        assertEquals(
            ORDERS_SORTED_BY_TRANSIT_TIMES,
            scheduler.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES)
        )
    }
}