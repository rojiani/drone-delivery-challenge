package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.PredictedRecommendation.DETRACTOR
import com.nrojiani.drone.model.PredictedRecommendation.NEUTRAL
import com.nrojiani.drone.model.PredictedRecommendation.PROMOTER
import com.nrojiani.drone.testutils.EPSILON
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class NPSCalculatorTest(
    private val recommendations: List<PredictedRecommendation>,
    private val nps: Double
) {

    @Test
    fun calculateNPS() {
        assertEquals(nps, calculateNPS(recommendations), EPSILON)
    }

    companion object {
        private val INPUT_1 = listOf(PROMOTER, PROMOTER, PROMOTER, PROMOTER)
        private const val OUTPUT_1 = 100.0

        private val INPUT_2 = listOf(PROMOTER, PROMOTER, PROMOTER, DETRACTOR)
        private const val OUTPUT_2 = 50.0

        private val INPUT_3 = listOf(PROMOTER, PROMOTER, DETRACTOR, DETRACTOR)
        private const val OUTPUT_3 = 0.0

        private val INPUT_4 = listOf(PROMOTER, PROMOTER, NEUTRAL, NEUTRAL, DETRACTOR, DETRACTOR)
        private const val OUTPUT_4 = 0.0

        private val INPUT_5 = listOf(NEUTRAL, NEUTRAL, DETRACTOR, DETRACTOR)
        private const val OUTPUT_5 = -50.0

        private val INPUT_6 = listOf(DETRACTOR, DETRACTOR, DETRACTOR, DETRACTOR)
        private const val OUTPUT_6 = -100.0

        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(INPUT_1, OUTPUT_1),
            arrayOf(INPUT_2, OUTPUT_2),
            arrayOf(INPUT_3, OUTPUT_3),
            arrayOf(INPUT_4, OUTPUT_4),
            arrayOf(INPUT_5, OUTPUT_5),
            arrayOf(INPUT_6, OUTPUT_6)
        )
    }
}