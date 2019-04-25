package com.nrojiani.drone.cli

import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.parser.parseOrders
import com.xenomachina.argparser.ArgParser

fun main(args: Array<String>) {
    val parsedArgs: DroneArgs = ArgParser(args).parseInto(::DroneArgs)
    parsedArgs.run {
        println("inputFilepath = $inputFilepath")
        val inputLines = readFileLines(inputFilepath)
        println("inputLines = $inputLines")

        val orders: List<Order> = parseOrders(inputLines)
        println(orders)
    }

    // TODO: print output file path
}
