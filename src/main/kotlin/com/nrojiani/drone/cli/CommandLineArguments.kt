package com.nrojiani.drone.cli

import com.xenomachina.argparser.ArgParser

/**
 * Uses Kotlin-Argparser to parse arguments from command line invocation.
 */
class CommandLineArguments(parser: ArgParser) {
    val inputFilepath by parser.storing(
        "-i", "--input",
        help = "Absolute path to input file"
    )
}