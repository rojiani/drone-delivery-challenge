package com.nrojiani.drone.model

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.testutils.Test1OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.utils.UTC_ZONE_ID
import org.junit.Test
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class DroneDeliveryTest {

    private val droneDelivery = DroneDelivery(
        orderWithTransitTime = PENDING_ORDER_1,
        timeOrderDelivered = ZonedDateTime.of(TODAY, LocalTime.parse("06:19:17"), UTC_ZONE_ID)
    )

    @Test
    fun timeOrderPlaced() {
        assertEquals(
            ZonedDateTime.of(TODAY, LocalTime.of(5, 11, 50), UTC_ZONE_ID),
            droneDelivery.timeOrderPlaced
        )
    }

    @Test
    fun timeDroneDeparted() {
        assertEquals(
            ZonedDateTime.of(TODAY, LocalTime.parse("06:07:12"), UTC_ZONE_ID),
            droneDelivery.timeDroneDeparted
        )
    }

    @Test
    fun timeDroneReturned() {
        assertEquals(
            ZonedDateTime.of(TODAY, LocalTime.parse("06:31:22"), UTC_ZONE_ID),
            droneDelivery.timeDroneReturned
        )
    }
}