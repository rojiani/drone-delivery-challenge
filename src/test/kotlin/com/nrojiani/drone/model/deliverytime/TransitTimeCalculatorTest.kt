package com.nrojiani.drone.model.deliverytime

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_MIN
import com.nrojiani.drone.testutils.EPSILON
import org.junit.Test

import org.junit.Assert.assertEquals
import kotlin.math.sqrt

class TransitTimeCalculatorTest {

    private val droneTimeCalc = TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_MIN)
    private val carTimeCalc = TransitTimeCalculator(4.0 * DRONE_SPEED_BLOCKS_PER_MIN)

    @Test
    fun calculateSourceToDestinationTime() {
        assertEquals(2.0, droneTimeCalc.calculateSourceToDestinationTime(2.0), EPSILON)
        assertEquals(0.5, carTimeCalc.calculateSourceToDestinationTime(2.0), EPSILON)
    }

    @Test
    fun calculateRoundTripTime() {
        assertEquals(4.0, droneTimeCalc.calculateRoundTripTime(2.0), EPSILON)
        assertEquals(1.0, carTimeCalc.calculateRoundTripTime(2.0), EPSILON)
    }

    @Test
    fun calculateSourceToDestinationTime_fromCoordinates() {
        assertEquals(
            sqrt(8.0), droneTimeCalc.calculateSourceToDestinationTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            ), EPSILON
        )
        assertEquals(
            sqrt(0.5), carTimeCalc.calculateSourceToDestinationTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            ), EPSILON
        )
    }

    @Test
    fun calculateRoundTripTime_fromCoordinates() {
        assertEquals(
            sqrt(32.0), droneTimeCalc.calculateRoundTripTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            ), EPSILON
        )
        assertEquals(
            sqrt(2.0), carTimeCalc.calculateRoundTripTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            ), EPSILON
        )
    }
}