package com.nrojiani.drone.cli

import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.parser.parseOrders
import com.xenomachina.argparser.ArgParser

/**
 * Entry point for application.
 */
fun main(args: Array<String>) {
    val parsedArgs: CommandLineArguments = ArgParser(args).parseInto(::CommandLineArguments)
    parsedArgs.run {
        println("inputFilepath = $inputFilepath")
        val inputLines = readFileLines(inputFilepath)
        println("inputLines = $inputLines")

        val orders: List<Order> = parseOrders(inputLines)
        println(orders)
    }

    // TODO: print output file path
}
