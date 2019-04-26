package com.nrojiani.drone.calculator

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.delivery.DroneDelivery

fun calculateNPS(deliveryCategories: Map<DroneDelivery, PredictedRecommendation>): Double {
    val percentPromoter: Double = deliveryCategories.values.toList().percentCategory(PredictedRecommendation.PROMOTER)
    val percentDetractor: Double = deliveryCategories.values.toList().percentCategory(PredictedRecommendation.DETRACTOR)
    return (percentPromoter - percentDetractor) * 100.0
}

private fun List<PredictedRecommendation>.percentCategory(category: PredictedRecommendation): Double = count {
    it == category
}.toDouble() / size.toDouble()

