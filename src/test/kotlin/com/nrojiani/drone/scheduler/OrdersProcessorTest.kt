package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_LAUNCH_FACILITY_LOCATION
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_SECOND
import com.nrojiani.drone.scheduler.calculator.TransitTimeCalculator
import com.nrojiani.drone.testutils.Test1OrderData
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class OrdersProcessorTest {

    private lateinit var ordersProcessor: OrdersProcessor

    @Before
    fun setUp() {
        ordersProcessor = OrdersProcessor(
            Test1OrderData.ORDERS,
            TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_SECOND),
            DRONE_LAUNCH_FACILITY_LOCATION
        )
    }

    @Test
    fun calculateTransitTimes() {
        val pendingDeliveries = ordersProcessor.calculateTransitTimes()
        assertEquals(Test1OrderData.ORDERS_WITH_TRANSIT_TIMES, pendingDeliveries)
    }
}