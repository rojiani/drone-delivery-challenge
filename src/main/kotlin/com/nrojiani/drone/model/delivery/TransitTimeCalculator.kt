package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.Coordinate
import kotlin.math.round

/**
 * Calculates travel time based solely on distance.
 * @param speed travel speed (in seconds)
 */
data class TransitTimeCalculator(private val speed: Double) {

    /**
     * Calculate the time (in seconds) to travel the specified [distance] (one-way) at a constant [speed].
     * Rounded to nearest second.
     */
    fun calculateSourceToDestinationTime(distance: Double): Long {
        require(distance >= 0.0 && distance.isFinite()) { "Invalid distance: $distance" }
        return round(distance / speed).toLong()
    }

    /**
     * Calculate the time (in seconds) to travel the distance (one-way) between the 2 specified points
     * at a constant [speed].
     */
    fun calculateSourceToDestinationTime(source: Coordinate, dest: Coordinate): Long =
        calculateSourceToDestinationTime(source.distanceTo(dest))

    /**
     * Calculate the time (in seconds) to travel the specified [distanceFromSourceToDest] and back at a constant
     * [speed].
     */
    fun calculateRoundTripTime(distanceFromSourceToDest: Double): Long =
            calculateSourceToDestinationTime(distanceFromSourceToDest) * 2

    /**
     * Calculate the time (in seconds) to travel from [source] to [dest] and then from [dest] to [source]
     * at a constant [speed].
     */
    fun calculateRoundTripTime(source: Coordinate, dest: Coordinate): Long =
        calculateRoundTripTime(source.distanceTo(dest))
}
