package com.nrojiani.drone.io.parser

/**
 * Exception indicating that the input could not be parsed.
 */
class OrderParsingException(message: String, cause: Throwable) : RuntimeException(message, cause)