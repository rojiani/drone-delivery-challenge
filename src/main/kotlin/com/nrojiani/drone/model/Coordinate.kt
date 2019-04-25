package com.nrojiani.drone.model

import kotlin.math.hypot

/**
 * Represents a 2D coordinate.
 */
data class Coordinate(val x: Double, val y: Double) {

    /**
     * Calculates the distance from (0, 0).
     */
    val distanceFromOrigin: Double
        get() = hypot(x, y)

    /** Create Grid Coordinate from integer coordinates */
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    /**
     * Calculates the distance between this coordinate and another
     */
    fun distanceTo(other: Coordinate): Double = hypot((other.x - x), (other.y - y))
}
