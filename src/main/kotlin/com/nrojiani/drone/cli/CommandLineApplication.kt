package com.nrojiani.drone.cli

import com.nrojiani.drone.io.output.OutputWriter
import com.nrojiani.drone.io.parser.parseOrders
import com.nrojiani.drone.io.readFileLines
import com.nrojiani.drone.model.DRONE_LAUNCH_FACILITY_LOCATION
import com.nrojiani.drone.model.DRONE_SPEED_BLOCKS_PER_SECOND
import com.nrojiani.drone.model.Order
import com.nrojiani.drone.model.delivery.TransitTime
import com.nrojiani.drone.model.delivery.TransitTimeCalculator
import com.nrojiani.drone.scheduler.DeliveryScheduler
import com.nrojiani.drone.scheduler.MinTransitTimeDeliveryScheduler
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
    private val scheduler: DeliveryScheduler by instance()

    // Dependency Injection
    override val kodein = Kodein {
        bind<TransitTimeCalculator>() with provider {
            TransitTimeCalculator(DRONE_SPEED_BLOCKS_PER_SECOND)
        }

        bind<CommandLineArguments>() with singleton {
            ArgParser(args).parseInto(::CommandLineArguments)
        }

        bind<DeliveryScheduler>() with singleton {
            MinTransitTimeDeliveryScheduler()
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
            println("orders with transit times: $orders")

            val deliveries = scheduler.scheduleDeliveries(orders)

            deliveries.forEach { println(it.timeOrderDelivered.toLocalTime()) }

            // TODO - Calculate NPS
            val nps = Double.POSITIVE_INFINITY

            // Write output file
            OutputWriter(deliveries, nps).writeOutputFile()
        }
    }

    // Set each order's TransitTime
    private fun addTransitTimes(orders: List<Order>) = orders.forEach { order ->
        order.transitTime = transitTimeCalculator.calculateSourceToDestinationTime(
            order.destination.distanceTo(DRONE_LAUNCH_FACILITY_LOCATION)
        ).run(::TransitTime)
    }
}