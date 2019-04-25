package com.nrojiani.drone.model

import com.nrojiani.drone.model.PredictedRecommendation.PROMOTER
import com.nrojiani.drone.model.PredictedRecommendation.NEUTRAL
import com.nrojiani.drone.model.PredictedRecommendation.DETRACTOR
import org.junit.Test

import org.junit.Assert.assertEquals
import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith

class PredictedRecommendationTest {

    @Test
    fun fromDeliveryTime() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(20))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(89))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(90))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(200))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(225))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(Int.MAX_VALUE))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTime(-1)
        }
    }

    @Test
    fun fromDeliveryTimeInHours() {
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(0.0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(1.0))
        assertEquals(PROMOTER, PredictedRecommendation.fromDeliveryTime(1.49))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(1.5))
        assertEquals(NEUTRAL, PredictedRecommendation.fromDeliveryTime(3.7))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(3.75))
        assertEquals(DETRACTOR, PredictedRecommendation.fromDeliveryTime(10.0))

        assertFailsWith<IllegalArgumentException> {
            PredictedRecommendation.fromDeliveryTime(-1.0)
        }
    }
}