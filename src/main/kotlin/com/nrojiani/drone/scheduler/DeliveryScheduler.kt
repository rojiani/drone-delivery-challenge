package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder

interface DeliveryScheduler {
    /**
     * Given a list of orders, calculate time & date data for each order.
     * Return a list of [DroneDelivery], which consists of [Order] and time-related data.
     */
    fun scheduleDeliveries(orders: List<PendingDeliveryOrder>): List<DroneDelivery>
}