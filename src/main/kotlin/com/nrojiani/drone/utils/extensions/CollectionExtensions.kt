package com.nrojiani.drone.utils.extensions

/**
 * Calculate the percentage of the elements matching the given [predicate].
 * @return ratio of matching elements as a decimal (e.g., 0.75).
 */
fun <T> Collection<T>.percentage(predicate: (T) -> Boolean): Double {
    val n = size.toDouble()
    val count = count { predicate(it) }
    return count.toDouble() / n
}
