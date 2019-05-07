package com.nrojiani.drone.cli

import com.nrojiani.drone.di.droneDeliverySchedulingModule
import com.nrojiani.drone.io.VerboseLogger
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

    /**
     * Run the application.
     */
    fun run() {
        val verboseModeEnabled = parsedArgs.verbose
        val verboseLogger = VerboseLogger(verboseModeEnabled)
        verboseLogger.logArgs(args, parsedArgs)

        val scheduler: DeliveryScheduler = determineScheduler(parsedArgs, verboseModeEnabled)

        /* Parse Orders */
        val orders: List<Order> = parseOrdersFromFile(
            parsedArgs.inputFilepath,
            parsedArgs.exitIfInvalid
        )
        logOrders(verboseLogger, orders)

        /* Calculates the distance and transit time for each order. */
        val estimatedOrders: List<PendingDeliveryOrder> = OrdersProcessor(
            orders,
            transitTimeCalculator,
            DRONE_LAUNCH_FACILITY_LOCATION
        ).calculateTransitTimes()
        logEstimatedOrders(verboseLogger, estimatedOrders)

        // Associate each order with metadata about delivery times.
        val deliveries: List<DroneDelivery> = scheduler.scheduleDeliveries(estimatedOrders)
        verboseLogger.logDeliveryTimes(deliveries)

        // Produce a list of Promoter Scores
        val predictedRecommendations: List<PredictedRecommendation> =
            deliveriesProcessor.predictedRecommendationsFor(deliveries)
        verboseLogger.logMessage("List<PredictedRecommendations>: $predictedRecommendations")

        // Write output file
        OutputWriter(deliveries, calculateNPS(predictedRecommendations)).writeOutputFile()
    }

    /** Determine the scheduler specified by the command-line args (or default if not provided) */
    private fun determineScheduler(parsedArgs: CommandLineArguments, verboseModeEnabled: Boolean): DeliveryScheduler =
        when (parsedArgs.schedulerName) {
            "MinTransitTimeDeliveryScheduler" -> MinTransitTimeDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS, schedulingDelegate
            )
            else -> PermutationOptimizingDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS,
                schedulingDelegate,
                deliveriesProcessor,
                VerboseLogger(verboseModeEnabled)
            )
        }

    private fun logOrders(verboseLogger: VerboseLogger, orders: List<Order>) = verboseLogger.logIterableAndKeys(
        elements = orders,
        elementsHeading = "Parsed Orders - List<Order>:",
        keysHeading = "orderIds:",
        keyLabel = "orderId"
    ) { it.orderId }

    private fun logEstimatedOrders(verboseLogger: VerboseLogger, estimatedOrders: List<PendingDeliveryOrder>) =
        verboseLogger.logIterableAndKeys(
            elements = estimatedOrders,
            elementsHeading = "Orders with Transit times calculated - List<PendingDeliveryOrder>:",
            keysHeading = "TransitTimes (1-Way, in seconds):",
            keyLabel = "transitTime"
        ) { it.transitTime.sourceToDestinationTime }
}