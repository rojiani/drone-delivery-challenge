@file:JvmName("DeliverySchedulingModule")

package com.nrojiani.drone.di

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_SECOND
import com.nrojiani.drone.scheduler.DeliveryScheduler
import com.nrojiani.drone.scheduler.MinTransitTimeDeliveryScheduler
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator
import com.nrojiani.drone.scheduler.calculator.OperatingHoursDeliveryTimeCalculator
import com.nrojiani.drone.scheduler.calculator.TransitTimeCalculator
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * Dependencies related to calculating and scheduling deliveries. The dependencies are constructed using the
 * constraints defined in the problem (e.g., [DRONE_DELIVERY_OPERATING_HOURS], [DRONE_SPEED_BLOCKS_PER_SECOND]).
 */
val droneDeliverySchedulingModule = Kodein.Module("Delivery Scheduling Module") {
    bind<TransitTimeCalculator>() with provider {
        TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_SECOND)
    }

    bind<DeliveryTimeCalculator>() with singleton {
        OperatingHoursDeliveryTimeCalculator(DRONE_DELIVERY_OPERATING_HOURS)
    }

    bind<DeliveryScheduler>() with singleton {
        MinTransitTimeDeliveryScheduler(DRONE_DELIVERY_OPERATING_HOURS)
    }
}