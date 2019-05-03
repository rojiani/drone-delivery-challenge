package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator

class DeliveriesProcessor(
    val deliveryTimeCalculator: DeliveryTimeCalculator
) {

    /**
     * Maps each delivery to its delivery time, as calculated by the implementation of [DeliveryTimeCalculator].
     */
    fun associateWithDeliveryTimes(deliveries: List<DroneDelivery>): Map<DroneDelivery, Long> = deliveries.associateBy(
        keySelector = { it },
        valueTransform = deliveryTimeCalculator::calculate
    )

    fun predictedRecommendationsFor(deliveries: List<DroneDelivery>): List<PredictedRecommendation> =
        associateWithDeliveryTimes(deliveries)
            .run(::predictedRecommendationsFor)

    fun predictedRecommendationsFor(deliveryTimesMap: Map<DroneDelivery, Long>): List<PredictedRecommendation> =
        deliveryTimesMap.values.map(PredictedRecommendation.Factory::fromDeliveryTime)
}
