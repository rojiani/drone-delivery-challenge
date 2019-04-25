package com.nrojiani.drone.model.deliverytime

import com.nrojiani.drone.model.Coordinate

/**
 * Calculates travel time based solely on distance.
 * @param speed travel speed (in minutes)
 */
data class TransitTimeCalculator(private val speed: Double) {

    /**
     * Calculate the time (in minutes) to travel the specified [distance] (one-way) at a constant [speed].
     */
    fun calculateSourceToDestinationTime(distance: Double): Double {
        require(distance >= 0.0 && distance.isFinite()) { "Invalid distance: $distance" }
        return distance / speed
    }

    /**
     * Calculate the time (in minutes) to travel the distance (one-way) between the 2 specified points
     * at a constant [speed].
     */
    fun calculateSourceToDestinationTime(source: Coordinate, dest: Coordinate): Double =
        calculateSourceToDestinationTime(source.distanceTo(dest))

    /**
     * Calculate the time (in minutes) to travel the specified [distanceFromSourceToDest] and back at a constant
     * [speed].
     */
    fun calculateRoundTripTime(distanceFromSourceToDest: Double): Double =
            calculateSourceToDestinationTime(distanceFromSourceToDest) * 2.0

    /**
     * Calculate the time (in minutes) to travel from [source] to [dest] and then from [dest] to [source]
     * at a constant [speed].
     */
    fun calculateRoundTripTime(source: Coordinate, dest: Coordinate): Double =
        calculateRoundTripTime(source.distanceTo(dest))
}
