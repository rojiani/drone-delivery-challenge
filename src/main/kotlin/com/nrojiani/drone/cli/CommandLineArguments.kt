package com.nrojiani.drone.cli

import arrow.core.Try
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.time.ZonedDateTime

/**
 * Uses Kotlin-Argparser to parse arguments from command line invocation.
 */
class CommandLineArguments(parser: ArgParser) {
    val inputFilepath by parser.storing(
        "-i", "--input",
        help = "Absolute path to input file"
    )

    val startTime by parser.storing(
        "-t", "--startTimestamp",
        help = "Timestamp for the time to start scheduling deliveries. Default is current UTC system time." +
                "Expected format: '2019-05-02T21:00:00Z'"
    ).default("")


    internal fun convertStartTime(): ZonedDateTime? = when {
            startTime.isNullOrBlank() -> null
            else -> Try { ZonedDateTime.parse(startTime) }
                .fold(
                    { e ->
                        throw RuntimeException(
                            "unable to parse arg -t ($startTime)." +
                                    "Expected format: '2019-05-02T21:00:00Z'"
                        )
                    },
                    { it }
                )
        }
}