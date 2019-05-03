package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator

class DeliveriesProcessor(
    private val deliveryTimeCalculator: DeliveryTimeCalculator
) {

    fun predictedRecommendationsFor(deliveries: List<DroneDelivery>): List<PredictedRecommendation> =
        associateWithDeliveryTimes(deliveries)
            .run(::predictedRecommendationsFor)

    fun predictedRecommendationsFor(deliveryTimesMap: Map<DroneDelivery, Long>): List<PredictedRecommendation> =
        deliveryTimesMap.values.map(PredictedRecommendation.Factory::fromDeliveryTime)

    /**
     * Maps each delivery to its delivery time, as calculated by the implementation of [DeliveryTimeCalculator].
     */
    private fun associateWithDeliveryTimes(deliveries: List<DroneDelivery>): Map<DroneDelivery, Long> = deliveries.associateBy(
        keySelector = { it },
        valueTransform = deliveryTimeCalculator::calculate
    )
}
