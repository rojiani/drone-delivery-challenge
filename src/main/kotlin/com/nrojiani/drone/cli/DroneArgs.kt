package com.nrojiani.drone.cli

import com.xenomachina.argparser.ArgParser

class DroneArgs(parser: ArgParser) {

    val inputFilepath by parser.storing(
        "-i", "--input",
        help = "Absolute path to input file"
    )

}