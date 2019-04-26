package com.nrojiani.drone.model.deliverytime

/**
 * The delivery time based solely on distance - does not represent actual delivery time, which can be influenced by
 * scheduling & hours of operation.
 * @param transitTimeToDestination The expected delivery time (in seconds) from starting point (e.g., a distribution
 *                                center) to destination.
 */
data class TransitTime(val transitTimeToDestination: Long) {
    val sourceToDestinationTime: Long
        get() = transitTimeToDestination

    /** Travel time (in seconds) from source to destination and then back to source */
    val roundTripTime: Long
        get() = transitTimeToDestination * 2
}