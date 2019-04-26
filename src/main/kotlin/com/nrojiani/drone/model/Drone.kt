@file:JvmName("Drone")

package com.nrojiani.drone.model

import com.nrojiani.drone.model.deliverytime.SECONDS_PER_MINUTE

const val DRONE_SPEED_BLOCKS_PER_MIN = 1.0
const val DRONE_SPEED_BLOCKS_PER_SECOND = DRONE_SPEED_BLOCKS_PER_MIN / SECONDS_PER_MINUTE

/**
 * The location of the drone-launching facility.
 */
@JvmField
val DRONE_LAUNCH_FACILITY_LOCATION = Coordinate(0, 0)