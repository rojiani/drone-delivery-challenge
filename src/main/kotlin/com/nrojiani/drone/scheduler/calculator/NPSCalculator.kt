package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.PredictedRecommendation.DETRACTOR
import com.nrojiani.drone.model.PredictedRecommendation.PROMOTER
import com.nrojiani.drone.utils.extensions.percentage

/**
 * Calculate the Net Promoter Score (NPS).
 *
 * ```
 * NPS = (% PROMOTER) - (% DETRACTOR)
 * ```
 */
fun calculateNPS(predictedRecs: Collection<PredictedRecommendation>): Double {
    val percentPromoter = predictedRecs.percentage { it == PROMOTER }
    val percentDetractor = predictedRecs.percentage { it == DETRACTOR }
    return (percentPromoter - percentDetractor) * 100.0
}
