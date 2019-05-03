package com.nrojiani.drone.cli

import com.nrojiani.drone.di.droneDeliverySchedulingModule
import com.nrojiani.drone.io.output.OutputWriter
import com.nrojiani.drone.io.parser.parseOrdersFromFile
import com.nrojiani.drone.model.DRONE_DELIVERY_OPERATING_HOURS
import com.nrojiani.drone.model.DRONE_LAUNCH_FACILITY_LOCATION
import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.scheduler.DeliveriesProcessor
import com.nrojiani.drone.scheduler.DeliveryScheduler
import com.nrojiani.drone.scheduler.MinTransitTimeDeliveryScheduler
import com.nrojiani.drone.scheduler.OrdersProcessor
import com.nrojiani.drone.scheduler.PermutationOptimizingDeliveryScheduler
import com.nrojiani.drone.scheduler.SchedulingDelegate
import com.nrojiani.drone.scheduler.calculator.TransitTimeCalculator
import com.nrojiani.drone.scheduler.calculator.calculateNPS
import com.nrojiani.drone.utils.extensions.formatted
import com.xenomachina.argparser.ArgParser
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

/**
 * Responsible for dependency injection & running the application.
 */
class CommandLineApplication(private val args: Array<String>) : KodeinAware {

    // Dependency Injection
    override val kodein = Kodein {
        bind<CommandLineArguments>() with provider {
            ArgParser(args).parseInto(::CommandLineArguments)
        }

        import(droneDeliverySchedulingModule)
    }

    // Lazily instantiated properties (resolved by DI container)
    private val parsedArgs: CommandLineArguments by instance()
    private val schedulingDelegate: SchedulingDelegate by instance()
    private val transitTimeCalculator: TransitTimeCalculator by instance()
    private val deliveriesProcessor: DeliveriesProcessor by instance()

    private var verboseModeEnabled: Boolean = false

    /**
     * Run the application.
     */
    fun run() {
        verboseModeEnabled = parsedArgs.verbose
        printArgumentsIfVerbose()

        val scheduler: DeliveryScheduler = when (parsedArgs.schedulerName) {
            "MinTransitTimeDeliveryScheduler" -> MinTransitTimeDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS, schedulingDelegate
            )
            else -> PermutationOptimizingDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS, schedulingDelegate, deliveriesProcessor, verboseModeEnabled
            )
        }

        /* Parse Orders */
        val orders: List<Order> = parseOrdersFromFile(
            parsedArgs.inputFilepath,
            parsedArgs.exitIfInvalid
        )
        printIterable(orders, "Parsed Orders - List<Order>:")
        printIterable(orders, "Parsed Orders - orderIds:", "orderId") { it.orderId }

        /* Calculates the distance and transit time for each order. */
        val estimatedOrders: List<PendingDeliveryOrder> =
            OrdersProcessor(orders, transitTimeCalculator, DRONE_LAUNCH_FACILITY_LOCATION)
                .calculateTransitTimes()

        printIterable(estimatedOrders, "Orders with Transit times calculated - List<PendingDeliveryOrder>:")
        printIterable(
            estimatedOrders,
            "TransitTimes (1-Way, in seconds)",
            "transitTime"
        ) { it.transitTime.sourceToDestinationTime }

        // Associate each order with metadata about delivery times.
        val deliveries: List<DroneDelivery> = scheduler.scheduleDeliveries(estimatedOrders)
        printDeliveryTimes(deliveries)

        // Produce a list of Promoter Scores
        val predictedRecommendations: List<PredictedRecommendation> =
            deliveriesProcessor.predictedRecommendationsFor(deliveries)
        printIfVerboseModeEnabled("List<PredictedRecommendations>: $predictedRecommendations")

        // Write output file
        OutputWriter(deliveries, calculateNPS(predictedRecommendations)).writeOutputFile()
    }

    private fun printDeliveryTimes(deliveries: List<DroneDelivery>) {
        printIterable(
            deliveries,
            "Delivery Times:\n" +
                    "\tid:       | Placed               | Drone Departed       | Delivered            | Returned ", ""
        ) {
            "id: %s | %s | %s | %s | %s".format(
                it.orderWithTransitTime.orderId,
                it.timeOrderPlaced.formatted,
                it.timeDroneDeparted.formatted,
                it.timeOrderDelivered.formatted,
                it.timeDroneReturned.formatted
            )
        }
    }

    private fun printIfVerboseModeEnabled(msg: String, newline: Boolean = false) {
        if (verboseModeEnabled) {
            println(msg)
        }
        if (newline) println()
    }

    private fun <T> printIterable(elements: Iterable<T>, label: String) {
        if (!verboseModeEnabled) return

        println(label)
        elements.forEach {
            println(it)
        }
        println()
    }

    private fun <T, K> printIterable(elements: Iterable<T>, label: String, keyLabel: String, keySelector: (T) -> K) {
        if (!verboseModeEnabled) return

        println(label)
        elements.forEach {
            val beforeKey = "\t$keyLabel${if (keyLabel.isNotBlank()) ": " else ""}"
            println("$beforeKey${keySelector(it)}")
        }
        println()
    }

    private fun printArgumentsIfVerbose() {
        printIfVerboseModeEnabled("args: ${args.contentToString()}")
        printIfVerboseModeEnabled("Verbose Mode (--verbose): On")
        printIfVerboseModeEnabled("Input filepath (--input): ${parsedArgs.inputFilepath}")
        printIfVerboseModeEnabled("Exit on invalid input (--exitOnInvalidInput): ${parsedArgs.exitIfInvalid}")
        printIfVerboseModeEnabled("DeliveryScheduler (--scheduler): ${parsedArgs.schedulerName}")
    }
}