package com.nrojiani.drone.scheduler.calculator

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.utils.extensions.percentage

/**
 * Calculate the Net Promoter Score (NPS).
 */
fun calculateNPS(recommendations: Collection<PredictedRecommendation>): Double {
    val percentPromoter = recommendations.percentage { it == PredictedRecommendation.PROMOTER }
    val percentDetractor = recommendations.percentage { it == PredictedRecommendation.DETRACTOR }
    return (percentPromoter - percentDetractor) * 100.0
}
