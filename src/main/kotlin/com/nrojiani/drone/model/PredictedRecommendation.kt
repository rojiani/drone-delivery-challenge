package com.nrojiani.drone.model

import com.nrojiani.drone.utils.MINUTES_PER_HOUR
import com.nrojiani.drone.utils.SECONDS_PER_MINUTE

/**
 * Likelihood of recommending service.
 * @param deliveryTimeRange delivery time range (in seconds)
 */
enum class PredictedRecommendation(val deliveryTimeRange: LongRange) {
    PROMOTER(0L until 5400L),
    NEUTRAL(5400L until 13500L),
    DETRACTOR(13500L..Long.MAX_VALUE);

    /** Factory methods */
    companion object Factory {
        fun fromDeliveryTime(seconds: Long): PredictedRecommendation {
            require(seconds >= 0L) { "Invalid delivery time: $seconds (must be >= 0)" }
            return values().first { seconds in it.deliveryTimeRange }
        }

        fun fromDeliveryTimeInMinutes(minutes: Long): PredictedRecommendation =
            fromDeliveryTime(seconds = minutes * SECONDS_PER_MINUTE)

        fun fromDeliveryTimeInHours(hours: Double): PredictedRecommendation =
            fromDeliveryTimeInMinutes(minutes = (hours * MINUTES_PER_HOUR).toLong())

        fun fromDeliveryTimeInHours(hours: Int): PredictedRecommendation =
            fromDeliveryTimeInHours(hours = hours.toDouble())
    }
}