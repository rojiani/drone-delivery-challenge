package com.nrojiani.drone.model.delivery

/**
 * The delivery time based solely on distance - does not represent actual delivery time, which can be influenced by
 * scheduling & hours of operation.
 * @param transitTimeToDestination The expected delivery time (in seconds) from starting point (e.g., a distribution
 *                                center) to destination.
 */
data class TransitTime(val transitTimeToDestination: Long) {
    val sourceToDestinationTime: Long
        get() = transitTimeToDestination
}