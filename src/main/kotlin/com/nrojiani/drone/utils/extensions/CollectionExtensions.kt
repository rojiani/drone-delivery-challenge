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

/**
 * Generates all permutations of the list.
 */
fun <T> List<T>.permutations(): Set<List<T>> = when (size) {
    0 -> setOf(emptyList())
    1 -> setOf(listOf(first()))
    2 -> setOf(this, listOf(this[1], this[0]))
    else -> {
        val permutations: MutableSet<List<T>> = hashSetOf()
        for (i in indices) {
            val otherElements = filterIndexed { index, _ -> index != i }
            val newPermutations = otherElements.permutations().map {
                it + this[i]
            }
            permutations.addAll(newPermutations)
        }
        permutations
    }
}