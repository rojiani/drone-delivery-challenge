package com.nrojiani.drone.testutils

import com.nrojiani.drone.model.Coordinate

/**
 * Maximum tolerated difference when comparing floating-point types for equality.
 */
const val EPSILON = 1e-10

/**
 * The ORIGIN on the coordinate system.
 */
@JvmField
val ORIGIN = Coordinate(0, 0)
