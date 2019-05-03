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

        import(droneDeliverySchedulingModule)
    }

    // Lazily properties (resolved by DI container)
    private val parsedArgs: CommandLineArguments by instance()
    private val schedulingDelegate: SchedulingDelegate by instance()
    private val scheduler: DeliveryScheduler by instance()
    private val transitTimeCalculator: TransitTimeCalculator by instance()
    private val deliveriesProcessor: DeliveriesProcessor by instance()

    /**
     * Run the application.
     */
    fun run() {

        val orders: List<Order> = parseOrdersFromFile(
            parsedArgs.inputFilepath,
            parsedArgs.exitIfInvalid
        )

//        println("orders: List<Order>")
//        orders.forEach {
//            println("${it.orderId} => placed @ ${it.orderPlacedDateTime}")
//        }
//        println()

        // Calculates the distance and transit time for each order.
        val estimatedOrders: List<PendingDeliveryOrder> =
            OrdersProcessor(orders, transitTimeCalculator, DRONE_LAUNCH_FACILITY_LOCATION)
                .calculateTransitTimes()

//        println("estimatedOrders: List<PendingDeliveryOrder>")
//        estimatedOrders.forEach {
//            println(it)
//        }
//        println()

//        println("estimatedOrders - transit times: List<TransitTime>")
//        estimatedOrders.forEach {
//            println(it.transitTime)
//        }
//        println()

        val scheduler: DeliveryScheduler = when (parsedArgs.schedulerName) {
            "MinTransitTimeDeliveryScheduler" -> MinTransitTimeDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS, schedulingDelegate
            )
            else -> PermutationOptimizingDeliveryScheduler(
                DRONE_DELIVERY_OPERATING_HOURS, schedulingDelegate, deliveriesProcessor
            )
        }
        println("scheduler: ${scheduler::class}")

        // Associate each order with metadata about delivery times.
        val deliveries: List<DroneDelivery> = scheduler.scheduleDeliveries(estimatedOrders)

//        println("deliveries: List<DroneDelivery>")
//        deliveries.forEach {
//            println(it)
//        }
//        println()

//        println("deliveries data")
//        println("id: placed / delivered / transit")
//        deliveries.forEach {
//            println("${it.orderWithTransitTime.orderId}: ${it.timeOrderPlaced} / ${it.timeOrderDelivered} / ${it.orderWithTransitTime.transitTime.sourceToDestinationTime}")
//        }
//        println()

        // Produce a list of Promoter Scores
        val predictedRecommendations: List<PredictedRecommendation> =
            deliveriesProcessor.predictedRecommendationsFor(deliveries)
//        println("predictedRecommendations: List<PredictedRecommendation>")
//        predictedRecommendations.forEach {
//            println(it)
//        }
//        println()

        // Write output file
        OutputWriter(deliveries, calculateNPS(predictedRecommendations))
            .writeOutputFile()
    }
}