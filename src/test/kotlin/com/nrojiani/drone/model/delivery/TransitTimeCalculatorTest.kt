package com.nrojiani.drone.model.delivery

import com.nrojiani.drone.model.Coordinate
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_SECOND
import com.nrojiani.drone.scheduler.calculator.TransitTimeCalculator
import com.nrojiani.drone.testutils.ORIGIN
import com.nrojiani.drone.utils.SECONDS_PER_MINUTE
import org.junit.Test
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.test.assertEquals

class TransitTimeCalculatorTest {

    private val droneTimeCalc = TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_SECOND)
    private val carTimeCalc = TransitTimeCalculator(4.0 * DRONE_SPEED_BLOCKS_PER_SECOND)

    @Test
    fun calculateSourceToDestinationTime() {
        assertEquals(120L, droneTimeCalc.calculateSourceToDestinationTime(2.0))
        assertEquals(30L, carTimeCalc.calculateSourceToDestinationTime(2.0))
    }

    @Test
    fun calculateRoundTripTime() {
        assertEquals(240L, droneTimeCalc.calculateRoundTripTime(2.0))
        assertEquals(60L, carTimeCalc.calculateRoundTripTime(2.0))
    }

    @Test
    fun `calculateSourceToDestinationTime - from Coordinates`() {
        assertEquals(
            round(sqrt(8.0) * SECONDS_PER_MINUTE).toLong(),
            droneTimeCalc.calculateSourceToDestinationTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            )
        )
        assertEquals(
            round(sqrt(0.5) * SECONDS_PER_MINUTE).toLong(),
            carTimeCalc.calculateSourceToDestinationTime(
                source = Coordinate(1.0, 2.0),
                dest = Coordinate(3.0, 4.0)
            )
        )
    }

    @Test
    fun `calculateRoundTripTime - from Coordinates`() {
        assertEquals(240L, droneTimeCalc.calculateRoundTripTime(ORIGIN, Coordinate(0.0, 2.0)))
        assertEquals(60L, carTimeCalc.calculateRoundTripTime(ORIGIN, Coordinate(0.0, 2.0)))
    }
}