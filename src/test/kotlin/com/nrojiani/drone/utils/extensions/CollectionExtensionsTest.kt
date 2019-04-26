package com.nrojiani.drone.utils.extensions

import com.nrojiani.drone.testutils.EPSILON
import org.junit.Test

import org.junit.Assert.assertEquals

class CollectionExtensionsTest {

    private enum class Animal { LION, TIGER, BEAR, WALRUS, TOUCAN }
    private val nums = listOf(1, 7, 10, 12, 14, 17, 79)
    private val animals = listOf(Animal.LION, Animal.WALRUS, Animal.TIGER, Animal.LION)

    @Test
    fun percentage() {
        assertEquals(3.0/7.0, nums.percentage { it % 2 == 0 }, EPSILON)
        assertEquals(4.0/7.0, nums.percentage { it % 2 != 0 }, EPSILON)
        assertEquals(0.25, animals.percentage { it == Animal.WALRUS }, EPSILON)
        assertEquals(0.50, animals.percentage { it == Animal.LION }, EPSILON)
    }
}