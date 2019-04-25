package com.nrojiani.drone.cli

import com.nrojiani.drone.io.readFileLines
import com.xenomachina.argparser.ArgParser

fun main(args: Array<String>) {
    println("main(args: ${args.toList()})")

    val parsedArgs: DroneArgs = ArgParser(args).parseInto(::DroneArgs)
    parsedArgs.run {
        println("inputFilepath = $inputFilepath")
        val lines = readFileLines(inputFilepath)
        println("lines = $lines")
    }

    // TODO: print output file path
}
