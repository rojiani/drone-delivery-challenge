package com.nrojiani.drone.model

import com.nrojiani.drone.model.PredictedRecommendation.DETRACTOR
import com.nrojiani.drone.model.PredictedRecommendation.NEUTRAL
import com.nrojiani.drone.model.PredictedRecommendation.PROMOTER
import com.nrojiani.drone.model.delivery.SECONDS_PER_MINUTE
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class PredictedRecommendationTest {

    @Test
    fun ranges() {
        val sToMinRange: (LongRange) -> LongRange = { sRange ->
            val start = (sRange.start.toDouble() / SECONDS_PER_MINUTE).toLong()
            if (sRange.endInclusive == Long.MAX_VALUE) start..Long.MAX_VALUE
            else start..(sRange.endInclusive.toDouble() / SECONDS_PER_MINUTE).toLong()
        }

        // See README for cutoff times.
        assertEquals(0L until 90L, sToMinRange(PredictedRecommendation.PROMOTER.deliveryTimeRange))
        assertEquals(90L until 225L, sToMinRange(PredictedRecommendation.NEUTRAL.deliveryTimeRange))
        assertEquals(225L..Long.MAX_VALUE, sToMinRange(PredictedRecommendation.DETRACTOR.deliveryTimeRange))
    }

    @Test
    fun fromDeliveryTime() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(0L))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(1200L))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(5399L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(5400L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(12000L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(13499L))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(13500L))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(Long.MAX_VALUE))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTime(-1L)
        }
    }

    @Test
    fun fromDeliveryTimeInMinutes() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInMinutes(0L))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInMinutes(60L))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInMinutes(89L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInMinutes(90L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInMinutes(120L))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInMinutes(180L))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInMinutes(240L))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInMinutes(600L))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTimeInMinutes(-1L)
        }
    }

    @Test
    fun `fromDeliveryTimeInHours - Int`() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInHours(0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInHours(1))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInHours(2))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInHours(3))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInHours(4))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInHours(10))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTimeInHours(-1)
        }
    }

    @Test
    fun `fromDeliveryTimeInHours - Double`() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInHours(0.0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInHours(1.0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTimeInHours(1.49))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInHours(1.5))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTimeInHours(3.7))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInHours(3.75))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTimeInHours(10.0))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTimeInHours(-1.0)
        }
    }
}