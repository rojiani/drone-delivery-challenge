package com.nrojiani.drone.cli

import com.nrojiani.drone.di.deliverySchedulingModule
import com.nrojiani.drone.io.output.OutputWriter
import com.nrojiani.drone.io.parser.parseOrdersFromFile
import com.nrojiani.drone.model.DRONE_LAUNCH_FACILITY_LOCATION
import com.nrojiani.drone.model.PredictedRecommendation
import com.nrojiani.drone.model.PredictedRecommendation.Companion.fromDeliveryTime
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.Order
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.scheduler.DeliveryScheduler
import com.nrojiani.drone.scheduler.OrdersProcessor
import com.nrojiani.drone.scheduler.calculator.DeliveryTimeCalculator
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

        import(deliverySchedulingModule)
    }

    // Lazily properties (resolved by DI container)
    private val parsedArgs: CommandLineArguments by instance()
    private val scheduler: DeliveryScheduler by instance()
    private val transitTimeCalculator: TransitTimeCalculator by instance()
    private val deliveryTimeCalculator: DeliveryTimeCalculator by instance()

    /**
     * Run the application.
     */
    fun run() {
        val orders: List<Order> = parseOrdersFromFile(parsedArgs.inputFilepath)

        // Calculates the distance and transit time for each order.
        val estimatedOrders: List<PendingDeliveryOrder> =
            OrdersProcessor(orders, transitTimeCalculator, DRONE_LAUNCH_FACILITY_LOCATION)
                .calculateTransitTimes()

        // Associate each order with metadata about delivery times.
        val deliveries: List<DroneDelivery> = scheduler.scheduleDeliveries(estimatedOrders)

        // Maps each delivery to its delivery time, as calculated by the implementation of
        // DeliveryTimeCalculator.
        val deliveryTimes: Map<DroneDelivery, Long> = deliveries.associateBy(
            keySelector = { it },
            valueTransform = deliveryTimeCalculator::calculate
        )

        // Produce a list of Promoter Scores
        val predictedRecommendations: List<PredictedRecommendation> = deliveryTimes.values.map(::fromDeliveryTime)

        val nps = calculateNPS(predictedRecommendations)

        // Write output file
        OutputWriter(deliveries, nps).writeOutputFile()
    }

}