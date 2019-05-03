package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import java.time.ZonedDateTime

interface DeliveryScheduler {
    /**
     * Given a list of orders, calculate time & date data for each order. Input orders are expected to have all been
     * placed on the same day.
     * @param pendingOrders A list of the orders pending delivery scheduling.
     * @param startTime (Optional) the time at which the first delivery should be scheduled for departure. If not
     *                  provided, the current time (or start of next operating hours) is used.
     * Return a list of [DroneDelivery], which consists of [Order] and time-related data.
     */
    fun scheduleDeliveries(
        pendingOrders: List<PendingDeliveryOrder>,
        startTime: ZonedDateTime? = null
    ): List<DroneDelivery>
}