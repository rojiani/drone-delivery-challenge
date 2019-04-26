@file:JvmName("Drone")

package com.nrojiani.drone.model

import com.nrojiani.drone.model.delivery.TimeInterval
import com.nrojiani.drone.model.delivery.SECONDS_PER_MINUTE
import java.time.LocalTime

const val DRONE_SPEED_BLOCKS_PER_MIN = 1.0
const val DRONE_SPEED_BLOCKS_PER_SECOND = DRONE_SPEED_BLOCKS_PER_MIN / SECONDS_PER_MINUTE

/**
 * The location of the drone-launching facility.
 */
@JvmField
val DRONE_LAUNCH_FACILITY_LOCATION = Coordinate(0, 0)

@JvmField
val DRONE_DELIVERY_OPERATING_HOURS = TimeInterval(LocalTime.parse("06:00:00"), LocalTime.parse("22:00:00"))