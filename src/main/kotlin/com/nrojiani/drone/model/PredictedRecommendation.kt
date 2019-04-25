package com.nrojiani.drone.model

/**
 * Likelihood of recommending service.
 * @param deliveryTimeRange delivery time range (in minutes)
 */
enum class PredictedRecommendation(val deliveryTimeRange: IntRange) {
    PROMOTER(0 until 90),
    NEUTRAL(90 until 225),
    DETRACTOR(225..Int.MAX_VALUE);

    /** Factory methods */
    companion object {
        fun fromDeliveryTime(minutes: Int): PredictedRecommendation {
            require(minutes >= 0) { "Invalid delivery time: $minutes (must be >= 0)" }
            return values().first { minutes in it.deliveryTimeRange }
        }

        fun fromDeliveryTime(hours: Double): PredictedRecommendation = fromDeliveryTime(minutes = (hours * 60).toInt())
    }
}