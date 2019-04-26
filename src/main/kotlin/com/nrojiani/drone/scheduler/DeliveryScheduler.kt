package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.Order

interface DeliveryScheduler {
    fun scheduleDeliveries(orders: List<Order>): List<DroneDelivery>
}