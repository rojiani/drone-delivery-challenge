package com.nrojiani.drone.scheduler

import com.nhaarman.mockitokotlin2.spy
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.testutils.OrderData.ORDERS_SORTED_BY_TRANSIT_TIMES
import com.nrojiani.drone.testutils.OrderData.ORDERS_WITH_TRANSIT_TIMES
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_2
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_3
import com.nrojiani.drone.testutils.OrderData.PENDING_ORDER_4
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.utils.EST_ZONE_ID
import com.nrojiani.drone.utils.EST_ZONE_OFFSET
import com.nrojiani.drone.utils.UTC_ZONE_ID
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MinTransitTimeDroneDeliverySchedulerTest {

    private val droneDeliveryScheduler = MinTransitTimeDeliveryScheduler(
        DRONE_DELIVERY_OPERATING_HOURS,
        Clock.systemUTC()
    )

    private val day1 = LocalDate.of(2019, 4, 25)
    private val day2 = LocalDate.of(2019, 4, 26)

    @Test
    fun scheduleDeliveries() {
        val scheduled = droneDeliveryScheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries are sorted by closest to furthest:
        assertEquals(
            listOf("WM002", "WM001", "WM004", "WM003"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        // Expected delivery times
        val expectedDeliveryTimes = EXPECTED_SCHEDULED_TODAY.map { it.timeOrderDelivered }
        val actualDeliveryTimes = scheduled.map { it.timeOrderDelivered }
        assertEquals(expectedDeliveryTimes, actualDeliveryTimes)

        // Everything
        assertEquals(EXPECTED_SCHEDULED_TODAY, scheduled)
    }

    @Test
    fun schedule() {
        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_TODAY,
                unscheduled = emptyList()
            ),
            droneDeliveryScheduler.schedule(
                ORDERS_SORTED_BY_TRANSIT_TIMES,
                ZonedDateTime.of(
                    TODAY, droneDeliveryScheduler.operatingHours.start.toLocalTime(),
                    UTC_ZONE_ID
                )
            )
        )
    }

    @Test
    fun ordersSortedByTransitTime() {
        assertEquals(
            ORDERS_SORTED_BY_TRANSIT_TIMES,
            droneDeliveryScheduler.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES)
        )
    }

    @Test
    fun `calculateFirstDeliveryStartTime - UTC`() {
        val beforeOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(5, 30, 0), UTC_ZONE_ID)
        val duringOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(12, 0, 0), UTC_ZONE_ID)
        val afterOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(23, 30, 0), UTC_ZONE_ID)

        assertFalse(beforeOpHoursUTC in DRONE_DELIVERY_OPERATING_HOURS.toZonedDateTimeInterval(day1, day1))
        assertTrue(duringOpHoursUTC in DRONE_DELIVERY_OPERATING_HOURS.toZonedDateTimeInterval(day1, day1))
        assertFalse(afterOpHoursUTC in DRONE_DELIVERY_OPERATING_HOURS.toZonedDateTimeInterval(day1, day1))

        val schedulerUTC = MinTransitTimeDeliveryScheduler(DRONE_DELIVERY_OPERATING_HOURS, Clock.systemUTC())

        // Use spy as partial mock to enable mocking the ZonedDateTime.now() call.
        val deliverySchedulerUTC = spy(schedulerUTC) {
            on { it.currentZonedDateTime }
                .thenReturn(beforeOpHoursUTC)
                .thenReturn(duringOpHoursUTC)
                .thenReturn(afterOpHoursUTC)
        }

        // Before Hours
        assertEquals(
            ZonedDateTime.of(day1, deliverySchedulerUTC.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            deliverySchedulerUTC.calculateFirstDeliveryStartTime()
        )

        // During Hours
        assertEquals(
            duringOpHoursUTC,
            deliverySchedulerUTC.calculateFirstDeliveryStartTime()
        )

        // After Hours
        assertEquals(
            ZonedDateTime.of(day2, deliverySchedulerUTC.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            deliverySchedulerUTC.calculateFirstDeliveryStartTime()
        )
    }

    @Test
    fun `calculateFirstDeliveryStartTime - EST`() {
        val clockEST = Clock.fixed(Instant.now(Clock.system(EST_ZONE_ID)), EST_ZONE_ID)
        val operatingHoursEST =
            TimeInterval(LocalTime.parse("06:00:00").atOffset(EST_ZONE_OFFSET), Duration.ofHours(16))
        val schedulerEST = MinTransitTimeDeliveryScheduler(operatingHoursEST, clockEST)

        val beforeOpHoursEST = ZonedDateTime.of(day1, LocalTime.of(5, 30, 0), EST_ZONE_ID)
        val duringOpHoursEST = ZonedDateTime.of(day1, LocalTime.of(12, 0, 0), EST_ZONE_ID)
        val afterOpHoursEST = ZonedDateTime.of(day1, LocalTime.of(23, 30, 0), EST_ZONE_ID)

        assertFalse(beforeOpHoursEST in operatingHoursEST.toZonedDateTimeInterval(day1, day1))
        assertTrue(duringOpHoursEST in operatingHoursEST.toZonedDateTimeInterval(day1, day1))
        assertFalse(afterOpHoursEST in operatingHoursEST.toZonedDateTimeInterval(day1, day1))

        // Use spy as partial mock to enable mocking the ZonedDateTime.now() call.
        val deliverySchedulerEST = spy(schedulerEST) {
            on { it.currentZonedDateTime }
                .thenReturn(beforeOpHoursEST)
                .thenReturn(duringOpHoursEST)
                .thenReturn(afterOpHoursEST)
        }

        // Before Hours
        assertEquals(
            ZonedDateTime.of(day1, deliverySchedulerEST.operatingHours.start.toLocalTime(), EST_ZONE_ID),
            deliverySchedulerEST.calculateFirstDeliveryStartTime()
        )

        // During Hours
        assertEquals(
            duringOpHoursEST,
            deliverySchedulerEST.calculateFirstDeliveryStartTime()
        )

        // After Hours
        assertEquals(
            ZonedDateTime.of(day2, deliverySchedulerEST.operatingHours.start.toLocalTime(), EST_ZONE_ID),
            deliverySchedulerEST.calculateFirstDeliveryStartTime()
        )
    }

    companion object {
        private val EXPECTED_SCHEDULED_TODAY = listOf(
            DroneDelivery(
                PENDING_ORDER_2, ZonedDateTime.of(
                    TODAY, LocalTime.parse("06:03:36"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_1, ZonedDateTime.of(
                    TODAY, LocalTime.parse("06:19:17"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_4, ZonedDateTime.of(
                    TODAY, LocalTime.parse("06:43:27"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_3, ZonedDateTime.of(
                    TODAY, LocalTime.parse("07:46:01"),
                    UTC_ZONE_ID
                )
            )
        )

//        private val EXPECTED_SCHEDULED_ORDER_DATE = EXPECTED_SCHEDULED_TODAY.map { delivery ->
//            DroneDelivery(
//                delivery.orderWithTransitTime,
//                ZonedDateTime.of(DATE_ORDER_PLACED, delivery.timeOrderDelivered.toLocalTime(), UTC_ZONE_ID)
//            )
//        }
    }
}