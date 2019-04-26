package com.nrojiani.drone.model

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.testutils.ORDER_1
import com.nrojiani.drone.testutils.TODAY
import org.junit.Test

import org.junit.Assert.assertEquals
import java.time.LocalDateTime
import java.time.LocalTime

class DroneDeliveryTest {

    private val droneDelivery = DroneDelivery(
        order = ORDER_1,
        timeOrderDelivered = LocalDateTime.of(TODAY, LocalTime.parse("06:19:17"))
    )

    @Test
    fun getTimeOrderPlaced() {
        assertEquals(LocalDateTime.of(TODAY, LocalTime.of(5, 11, 50)), droneDelivery.timeOrderPlaced)
    }

    @Test
    fun getTimeDroneReturned() {
        assertEquals(
            LocalDateTime.of(TODAY, LocalTime.parse("06:31:21")),
            droneDelivery.timeDroneReturned
        )
    }
}