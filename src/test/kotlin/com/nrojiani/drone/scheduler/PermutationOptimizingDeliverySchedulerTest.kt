package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator
import org.junit.Ignore
import org.junit.Test

class PermutationOptimizingDeliverySchedulerTest {

    private val scheduler = PermutationOptimizingDeliveryScheduler(
        operatingHours = DRONE_DELIVERY_OPERATING_HOURS,
        delegate = SchedulingDelegate(DRONE_DELIVERY_OPERATING_HOURS),
        deliveriesProcessor = DeliveriesProcessor(
            OperatingHoursDeliveryTimeCalculator(DRONE_DELIVERY_OPERATING_HOURS)
        ),
        verboseModeEnabled = true
    )

    @Ignore("Unimplemented")
    @Test
    fun `scheduleDeliveries - test input 1`() {}
}