package com.nrojiani.drone.io.parser

import java.lang.RuntimeException

/**
 * Exception indicating that the input could not be parsed.
 */
class OrderParsingException(message: String, cause: Throwable) : RuntimeException(message, cause)