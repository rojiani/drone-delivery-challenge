package com.nrojiani.drone.model

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.TODAY
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class DroneDeliveryTest {

    private val droneDelivery = DroneDelivery(
        orderWithTransitTime = PENDING_ORDER_1,
        timeOrderDelivered = LocalDateTime.of(TODAY, LocalTime.parse("06:19:17"))
    )

    @Test
    fun timeOrderPlaced() {
        assertEquals(LocalDateTime.of(TODAY, LocalTime.of(5, 11, 50)), droneDelivery.timeOrderPlaced)
    }

    @Test
    fun timeDroneDeparted() {
        assertEquals(
            LocalDateTime.of(TODAY, LocalTime.parse("06:07:13")),
            droneDelivery.timeDroneDeparted
        )
    }

    @Test
    fun timeDroneReturned() {
        assertEquals(
            LocalDateTime.of(TODAY, LocalTime.parse("06:31:21")),
            droneDelivery.timeDroneReturned
        )
    }
}