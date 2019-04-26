package com.nrojiani.drone.scheduler

import com.nrojiani.drone.testutils.ORDER_1
import com.nrojiani.drone.testutils.ORDER_2
import com.nrojiani.drone.testutils.ORDER_3
import com.nrojiani.drone.testutils.ORDER_4
import org.junit.Test
import kotlin.test.assertEquals

class MinTransitTimeDroneDeliverySchedulerTest {

    private val droneDeliveryScheduler = MinTransitTimeDeliveryScheduler()
    private val orders = listOf(ORDER_1, ORDER_2, ORDER_3, ORDER_4)

    @Test
    fun scheduleDeliveries() {
    }

    @Test
    fun ordersSortedByTransitTime() {
        val expected = listOf(ORDER_2, ORDER_1, ORDER_4, ORDER_3)
        assertEquals(expected, droneDeliveryScheduler.ordersSortedByTransitTime(orders))
    }
}