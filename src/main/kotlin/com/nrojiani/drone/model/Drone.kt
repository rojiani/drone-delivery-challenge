@file:JvmName("Drone")

package com.nrojiani.drone.model

import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.utils.DEFAULT_ZONE_OFFSET
import com.nrojiani.drone.utils.SECONDS_PER_MINUTE
import java.time.Duration
import java.time.LocalTime

const val DRONE_SPEED_BLOCKS_PER_MIN = 1.0
const val DRONE_SPEED_BLOCKS_PER_SECOND = DRONE_SPEED_BLOCKS_PER_MIN / SECONDS_PER_MINUTE

/**
 * The location of the drone-launching facility.
 */
@JvmField
val DRONE_LAUNCH_FACILITY_LOCATION = Coordinate(0, 0)

/** 6am - 10pm */
@JvmField
val DRONE_DELIVERY_OPERATING_HOURS =
    TimeInterval(LocalTime.parse("06:00:00").atOffset(DEFAULT_ZONE_OFFSET), Duration.ofHours(16))

/** 10pm - 6am */
@JvmField
val DRONE_DELIVERY_OFF_HOURS =
    TimeInterval(LocalTime.parse("22:00:00").atOffset(DEFAULT_ZONE_OFFSET), Duration.ofHours(8))
