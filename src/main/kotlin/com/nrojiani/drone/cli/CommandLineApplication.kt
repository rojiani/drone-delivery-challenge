package com.nrojiani.drone.cli

import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.DRONE_LAUNCH_FACILITY_LOCATION
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_SECOND
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.model.deliverytime.TransitTime
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
            TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_SECOND)
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
            val orderInputLines = readFileLines(inputFilepath)
            val orders: List<Order> = parseOrders(orderInputLines)

            addTransitTimes(orders)

            println(orders)
        }

        // TODO: print output file path
    }

    // Set each order's TransitTime
    private fun addTransitTimes(orders: List<Order>) = orders.forEach { order ->
        order.transitTime = transitTimeCalculator.calculateSourceToDestinationTime(
            order.destination.distanceTo(DRONE_LAUNCH_FACILITY_LOCATION)
        ).run(::TransitTime)

        println("(${order.orderId}): " +
                "orderPlacedAt: ${order.orderPlacedDateTime.toLocalTime()} " +
                "${order.destination.distanceFromOrigin} blocks => " +
                "${order.transitTime!!.sourceToDestinationTime} s")
    }
}