package com.nrojiani.drone.cli

import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_MIN
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.model.deliverytime.TransitTimeCalculator
import com.nrojiani.drone.parser.parseOrders
import com.xenomachina.argparser.ArgParser
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * Entry point for application.
 */
fun main(args: Array<String>) {
    println("Main: main(args=${args.toList()})")
    val app = CommandLineApplication(args)
    app.run()
}

/**
 * Responsible for dependency injection & running the application.
 */
class CommandLineApplication(private val args: Array<String>) : KodeinAware {

    private val transitTimeCalculator: TransitTimeCalculator by instance()
    private val parsedArgs: CommandLineArguments by instance()

    // Dependency Injection
    override val kodein = Kodein {
        bind<TransitTimeCalculator>() with provider {
            TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_MIN)
        }

        bind<CommandLineArguments>() with singleton {
            ArgParser(args).parseInto(::CommandLineArguments)
        }
    }

    /**
     * Entry point for application.
     */
    fun run() {
        parsedArgs.run {
            println("inputFilepath = $inputFilepath")
            val inputLines = readFileLines(inputFilepath)
            println("inputLines = $inputLines")

            val orders: List<Order> = parseOrders(inputLines)

            orders.forEach { order ->
                val distance = order.destination.distanceFromOrigin
                val transitTime = transitTimeCalculator.calculateSourceToDestinationTime(distance)
                println("order = $order")
                println("distance = $distance")
                println("transitTime = $transitTime")
            }
        }

        // TODO: print output file path
    }
}