package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.delivery.DroneDelivery
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
import com.nrojiani.drone.utils.UTC_ZONE_ID
import org.junit.Test
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MinTransitTimeDroneDeliverySchedulerTest {

    private val scheduler = MinTransitTimeDeliveryScheduler(DRONE_DELIVERY_OPERATING_HOURS)

    private val ordersWithRollovers = Test2OrderData.ORDERS_WITH_TRANSIT_TIMES

    @Test
    fun `scheduleDeliveries - all scheduled`() {
        val scheduled = scheduler.scheduleDeliveries(ORDERS_WITH_TRANSIT_TIMES)

        // Deliveries are sorted by closest to furthest:
        assertEquals(
            listOf("WM002", "WM001", "WM004", "WM003"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        // Expected delivery times
        assertEquals(
            scheduled.map { it.timeOrderDelivered },
            EXPECTED_SCHEDULED_NO_ROLLOVER.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_NO_ROLLOVER, scheduled)
    }

    @Test
    fun `scheduleDeliveries - some rollovers`() {
        val scheduled = scheduler.scheduleDeliveries(Test2OrderData.ORDERS_WITH_TRANSIT_TIMES)

        assertEquals(
            listOf("WM005", "WM006", "WM007", "WM008"),
            scheduled.map { it.orderWithTransitTime.orderId }
        )

        assertEquals(
            EXPECTED_SCHEDULED_WITH_ROLLOVER.map { it.timeOrderDelivered },
            scheduled.map { it.timeOrderDelivered }
        )

        assertEquals(EXPECTED_SCHEDULED_WITH_ROLLOVER, scheduled)
    }

    @Test
    fun `schedule - all scheduled`() {
        val deliveryStartTime = scheduler.calculateFirstDeliveryStartTime(
            ORDERS_WITH_TRANSIT_TIMES.first().dateTimeOrderPlaced
        )

        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_NO_ROLLOVER,
                unscheduled = emptyList()
            ),
            scheduler.schedule(
                ORDERS_SORTED_BY_TRANSIT_TIMES,
                ZonedDateTime.of(deliveryStartTime.toLocalDate(), deliveryStartTime.toLocalTime(), UTC_ZONE_ID)
            )
        )
    }

    @Test
    fun `schedule - some rollovers`() {
        // All orders placed at 21:00:00. Run scheduler at same time.
        val timeOrdersPlaced = ordersWithRollovers.first().dateTimeOrderPlaced
        val deliveryStartTime = scheduler.calculateFirstDeliveryStartTime(timeOrdersPlaced)

        val schedulingResult = scheduler.schedule(ordersWithRollovers, deliveryStartTime)

        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_WITH_ROLLOVER.dropLast(1),
                unscheduled = listOf(PENDING_ORDER_8)
            ),
            schedulingResult
        )
    }

    // TODO - test-input-3

    @Test
    fun ordersSortedByTransitTime() {
        assertEquals(
            ORDERS_SORTED_BY_TRANSIT_TIMES,
            scheduler.ordersSortedByTransitTime(ORDERS_WITH_TRANSIT_TIMES)
        )
    }

    @Test
    fun calculateFirstDeliveryStartTime() {
        val dateTimeFirstOrderPlaced = ORDERS_WITH_TRANSIT_TIMES.first().dateTimeOrderPlaced
        val dateOrderPlaced = dateTimeFirstOrderPlaced.toLocalDate()

        val beforeOpHoursUTC = ZonedDateTime.of(dateOrderPlaced, LocalTime.of(5, 30, 0), UTC_ZONE_ID)
        val duringOpHoursUTC = ZonedDateTime.of(dateOrderPlaced, LocalTime.of(12, 0, 0), UTC_ZONE_ID)
        val afterOpHoursUTC = ZonedDateTime.of(dateOrderPlaced, LocalTime.of(23, 30, 0), UTC_ZONE_ID)

        val datedOpHours = DRONE_DELIVERY_OPERATING_HOURS.toZonedDateTimeInterval(dateOrderPlaced, dateOrderPlaced)

        assertFalse(beforeOpHoursUTC in datedOpHours)
        assertTrue(duringOpHoursUTC in datedOpHours)
        assertFalse(afterOpHoursUTC in datedOpHours)

        // Before Hours
        assertEquals(
            ZonedDateTime.of(dateOrderPlaced, scheduler.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            scheduler.calculateFirstDeliveryStartTime(beforeOpHoursUTC)
        )

        // During Hours
        assertEquals(
            duringOpHoursUTC,
            scheduler.calculateFirstDeliveryStartTime(duringOpHoursUTC)
        )

        // After Hours
        assertEquals(
            ZonedDateTime.of(dateOrderPlaced.plusDays(1), scheduler.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            scheduler.calculateFirstDeliveryStartTime(afterOpHoursUTC)
        )
    }

    companion object {
        private val EXPECTED_SCHEDULED_NO_ROLLOVER = listOf(
            DroneDelivery(
                PENDING_ORDER_2, ZonedDateTime.of(
                    PENDING_ORDER_2.dateOrderPlaced, LocalTime.parse("06:03:36"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_1, ZonedDateTime.of(
                    PENDING_ORDER_1.dateOrderPlaced, LocalTime.parse("06:19:17"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_4, ZonedDateTime.of(
                    PENDING_ORDER_4.dateOrderPlaced, LocalTime.parse("06:43:27"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_3, ZonedDateTime.of(
                    PENDING_ORDER_3.dateOrderPlaced, LocalTime.parse("07:46:01"),
                    UTC_ZONE_ID
                )
            )
        )

        private val EXPECTED_SCHEDULED_WITH_ROLLOVER = listOf(
            DroneDelivery(
                PENDING_ORDER_5, ZonedDateTime.of(
                    PENDING_ORDER_5.dateOrderPlaced, LocalTime.parse("21:10:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_6, ZonedDateTime.of(
                    PENDING_ORDER_6.dateOrderPlaced, LocalTime.parse("21:30:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_7, ZonedDateTime.of(
                    PENDING_ORDER_7.dateOrderPlaced, LocalTime.parse("21:50:00"),
                    UTC_ZONE_ID
                )
            ),
            DroneDelivery(
                PENDING_ORDER_8, ZonedDateTime.of(
                    PENDING_ORDER_8.dateOrderPlaced.plusDays(1), LocalTime.parse("06:10:00"),
                    UTC_ZONE_ID
                )
            )
        )
    }
}