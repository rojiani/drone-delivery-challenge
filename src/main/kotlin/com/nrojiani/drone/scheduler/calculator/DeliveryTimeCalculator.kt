package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.model.delivery.DroneDelivery

/**
 * Implementing classes can determine how to calculate delivery time.
 */
interface DeliveryTimeCalculator {
    /**
     * Return the delivery time in seconds.
     */
    fun calculate(droneDelivery: DroneDelivery): Long
}