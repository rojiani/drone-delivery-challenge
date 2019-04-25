package com.nrojiani.drone.model

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.sqrt
import kotlin.test.assertEquals

private const val EPSILON = 1e-10

class CoordinateTest {

    private val origin = Coordinate(0, 0)

    @Test
    fun distanceTo() {
        assertEquals(3.0, origin.distanceTo(Coordinate(3, 0)), EPSILON)
        assertEquals(2.0, origin.distanceTo(Coordinate(0, 2)), EPSILON)
        assertEquals(sqrt(2.0), origin.distanceTo(Coordinate(1, 1)), EPSILON)
        assertEquals(sqrt(58.0), origin.distanceTo(Coordinate(3, 7)), EPSILON)

        assertEquals(3.0, origin.distanceTo(Coordinate(-3, 0)), EPSILON)
        assertEquals(sqrt(97.0), origin.distanceTo(Coordinate(-4, -9)), EPSILON)

        assertEquals(7.496665925596525, origin.distanceTo(Coordinate(1.2, 7.4)), EPSILON)

        assertEquals(sqrt(8.0), Coordinate(2.0, 2.0).distanceTo(Coordinate(4.0, 4.0)), EPSILON)
        assertEquals(sqrt(25.0 + 36.0), Coordinate(2.0, 3.0).distanceTo(Coordinate(7.0, 9.0)), EPSILON)
    }

    @Test
    fun distanceFromOrigin() {
        assertEquals(1.0, Coordinate(1, 0).distanceFromOrigin(), EPSILON)
        assertEquals(1.0, Coordinate(0, 1).distanceFromOrigin(), EPSILON)
        assertEquals(3.0, Coordinate(0, 3).distanceFromOrigin(), EPSILON)
        assertEquals(sqrt(2.0), Coordinate(1, 1).distanceFromOrigin(), EPSILON)
        assertEquals(sqrt(58.0), Coordinate(3, 7).distanceFromOrigin(), EPSILON)

        assertEquals(3.0, Coordinate(-3, 0).distanceFromOrigin(), EPSILON)
        assertEquals(sqrt(97.0), Coordinate(-4, -9).distanceFromOrigin(), EPSILON)

        assertEquals(7.496665925596525, Coordinate(1.2, 7.4).distanceFromOrigin(), EPSILON)
    }

    @Test
    fun secondaryConstructor() {
        assertEquals(Coordinate(1.0, 9.0), Coordinate(1, 9))
    }
}