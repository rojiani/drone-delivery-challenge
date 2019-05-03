package com.nrojiani.drone.scheduler

import com.nhaarman.mockitokotlin2.spy
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.testutils.TODAY
import com.nrojiani.drone.testutils.Test1OrderData.ORDERS_SORTED_BY_TRANSIT_TIMES
import com.nrojiani.drone.testutils.Test1OrderData.ORDERS_WITH_TRANSIT_TIMES
import com.nrojiani.drone.testutils.Test1OrderData.PENDING_ORDER_1
import com.nrojiani.drone.testutils.Test1OrderData.PENDING_ORDER_2
import com.nrojiani.drone.testutils.Test1OrderData.PENDING_ORDER_3
import com.nrojiani.drone.testutils.Test1OrderData.PENDING_ORDER_4
import com.nrojiani.drone.testutils.Test2OrderData
import com.nrojiani.drone.testutils.Test2OrderData.PENDING_ORDER_5
import com.nrojiani.drone.testutils.Test2OrderData.PENDING_ORDER_6
import com.nrojiani.drone.testutils.Test2OrderData.PENDING_ORDER_7
import com.nrojiani.drone.testutils.Test2OrderData.PENDING_ORDER_8
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

    private val day1 = LocalDate.of(2019, 4, 25)
    private val day2 = LocalDate.of(2019, 4, 26)

    private val beforeOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(5, 30, 0), UTC_ZONE_ID)
    private val duringOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(12, 0, 0), UTC_ZONE_ID)
    private val afterOpHoursUTC = ZonedDateTime.of(day1, LocalTime.of(23, 30, 0), UTC_ZONE_ID)

    private val schedulerUTC = MinTransitTimeDeliveryScheduler(
        DRONE_DELIVERY_OPERATING_HOURS,
        Clock.systemUTC()
    )

    // Use spy as partial mock to enable mocking the ZonedDateTime.now() call.
    val spyScheduler = spy(schedulerUTC) {
        on { it.currentZonedDateTime }
            .thenReturn(beforeOpHoursUTC)
            .thenReturn(duringOpHoursUTC)
            .thenReturn(afterOpHoursUTC)
    }

    private val ordersWithRollovers = Test2OrderData.ORDERS_WITH_TRANSIT_TIMES

    @Test
    fun `scheduleDeliveries - all scheduled`() {
        val timeFirstOrderPlaced = ORDERS_WITH_TRANSIT_TIMES.first().dateTimeOrderPlaced
        val scheduler = spy(schedulerUTC) {
            on { it.currentZonedDateTime }
                .thenReturn(timeFirstOrderPlaced)
        }
        val scheduled = scheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries are sorted by closest to furthest:
        assertEquals(
            listOf("WM002", "WM001", "WM004", "WM003"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        assertEquals(
            scheduled.map { it.timeOrderDelivered },
            EXPECTED_SCHEDULED_TODAY_INPUT_1.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_TODAY_INPUT_1, scheduled)
    }

    @Test
    fun `scheduleDeliveries - some rollovers`() {
        // All orders placed at 21:00:00. Run scheduler at same time.
        val timeOrdersPlaced = ordersWithRollovers.first().dateTimeOrderPlaced
        val scheduler = spy(schedulerUTC) {
            on { it.currentZonedDateTime }
                .thenReturn(timeOrdersPlaced)
        }
        val scheduled = scheduler.scheduleDeliveries(Test2OrderData.ORDERS_WITH_TRANSIT_TIMES)

        assertEquals(
            listOf("WM005", "WM006", "WM007", "WM008"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        assertEquals(
            EXPECTED_SCHEDULED_INPUT_2.map { it.timeOrderDelivered },
            scheduled.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_INPUT_2, scheduled)
    }

    // TODO: test startTime param

    @Test
    fun `schedule - all scheduled`() {
        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_TODAY_INPUT_1,
                unscheduled = emptyList()
            ),
            spyScheduler.schedule(
                ORDERS_SORTED_BY_TRANSIT_TIMES,
                ZonedDateTime.of(TODAY, spyScheduler.operatingHours.start.toLocalTime(), UTC_ZONE_ID)
            )
        )
    }

    @Test
    fun `schedule - some rollovers`() {
        // All orders placed at 21:00:00. Run scheduler at same time.
        val timeOrdersPlaced = ordersWithRollovers.first().dateTimeOrderPlaced
        val scheduler = spy(schedulerUTC) {
            on { it.currentZonedDateTime }
                .thenReturn(timeOrdersPlaced)
        }

        val schedulingResult = scheduler.schedule(
            ordersWithRollovers,
            timeOrdersPlaced
        )

        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_INPUT_2.dropLast(1),
                unscheduled = listOf(PENDING_ORDER_8)
            ),
            schedulingResult
        )
    }

    @Test
    fun ordersSortedByTransitTime() {
        assertEquals(
            ORDERS_SORTED_BY_TRANSIT_TIMES,
            schedulerUTC.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES)
        )
    }

    @Test
    fun `calculateFirstDeliveryStartTime - UTC`() {
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
        private val EXPECTED_SCHEDULED_TODAY_INPUT_1 = listOf(
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

        private val EXPECTED_SCHEDULED_INPUT_2 = listOf(
            DroneDelivery(
                PENDING_ORDER_5, ZonedDateTime.of(
                    TODAY, LocalTime.parse("21:10:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_6, ZonedDateTime.of(
                    TODAY, LocalTime.parse("21:30:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_7, ZonedDateTime.of(
                    TODAY, LocalTime.parse("21:50:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_8, ZonedDateTime.of(
                    TODAY.plusDays(1), LocalTime.parse("06:10:00"),
                    UTC_ZONE_ID
                )
            )
        )
    }
}