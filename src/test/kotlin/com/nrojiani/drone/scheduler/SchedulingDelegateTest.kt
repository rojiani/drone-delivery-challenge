package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.testutils.Test1OrderData
import com.nrojiani.drone.testutils.Test2OrderData
import com.nrojiani.drone.testutils.Test2OrderData.EXPECTED_SCHEDULED_NO_ROLLOVER
import com.nrojiani.drone.testutils.Test2OrderData.EXPECTED_SCHEDULED_WITH_ROLLOVER
import com.nrojiani.drone.utils.UTC_ZONE_ID
import org.junit.Test
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SchedulingDelegateTest {

    private val delegate = SchedulingDelegate(DRONE_DELIVERY_OPERATING_HOURS)
    private val ordersWithRollovers = Test2OrderData.ORDERS_WITH_TRANSIT_TIMES

    @Test
    fun `schedule - all scheduled`() {
        val deliveryStartTime = delegate.calculateFirstDeliveryStartTime(
            Test1OrderData.ORDERS_WITH_TRANSIT_TIMES.first().dateTimeOrderPlaced
        )

        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_NO_ROLLOVER,
                unscheduled = emptyList()
            ),
            delegate.schedule(
                Test1OrderData.ORDERS_SORTED_BY_TRANSIT_TIMES,
                ZonedDateTime.of(deliveryStartTime.toLocalDate(), deliveryStartTime.toLocalTime(), UTC_ZONE_ID)
            )
        )
    }

    @Test
    fun `schedule - some rollovers`() {
        // All orders placed at 21:00:00. Run scheduler at same time.
        val timeOrdersPlaced = ordersWithRollovers.first().dateTimeOrderPlaced
        val deliveryStartTime = delegate.calculateFirstDeliveryStartTime(timeOrdersPlaced)

        val schedulingResult = delegate.schedule(ordersWithRollovers, deliveryStartTime)

        assertEquals(
            SchedulingResult(
                scheduled = EXPECTED_SCHEDULED_WITH_ROLLOVER.dropLast(1),
                unscheduled = listOf(Test2OrderData.PENDING_ORDER_8)
            ),
            schedulingResult
        )
    }

    // TODO - test-input-3

    @Test
    fun calculateFirstDeliveryStartTime() {
        val dateTimeFirstOrderPlaced = Test1OrderData.ORDERS_WITH_TRANSIT_TIMES.first().dateTimeOrderPlaced
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
            ZonedDateTime.of(dateOrderPlaced, delegate.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            delegate.calculateFirstDeliveryStartTime(beforeOpHoursUTC)
        )

        // During Hours
        assertEquals(
            duringOpHoursUTC,
            delegate.calculateFirstDeliveryStartTime(duringOpHoursUTC)
        )

        // After Hours
        assertEquals(
            ZonedDateTime.of(dateOrderPlaced.plusDays(1), delegate.operatingHours.start.toLocalTime(), UTC_ZONE_ID),
            delegate.calculateFirstDeliveryStartTime(afterOpHoursUTC)
        )
    }
}