package com.nrojiani.drone.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * Uses Kotlin-Argparser to parse arguments from command line invocation.
 */
class CommandLineArguments(parser: ArgParser) {
    val inputFilepath by parser.storing(
        "-i", "--input",
        help = "Absolute path to input file"
    )

    val exitOnInvalidInput: String by parser.storing(
        "-x", "--exitOnInvalidInput",
        help = "If true, application will exit if input contains invalid lines. Otherwise, invalid lines will be skipped."
    ).default("")

    val schedulerName by parser.storing(
        "-s", "--scheduler",
        help = "The DeliveryScheduler implementation to use, e.g., MinTransitTimeDeliveryScheduler"
    ).default("PermutationOptimizingDeliveryScheduler")

    // TODO verbose mode

    val exitIfInvalid: Boolean = when {
        exitOnInvalidInput.isNullOrBlank() -> true
        exitOnInvalidInput.equals("true", ignoreCase = true) -> true
        else -> false
    }
}