package com.nrojiani.drone.utils.extensions

/**
 * Format the [Double] as a String with [n] decimal places.
 */
fun Double.formatToNDecimalPlaces(n: Int): String = "%.${n}f".format(this)