package com.nrojiani.drone.scheduler

import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.order.PendingDeliveryOrder
import com.nrojiani.drone.model.time.TimeInterval
import com.nrojiani.drone.scheduler.calculator.calculateNPS
import com.nrojiani.drone.utils.DEFAULT_ZONE_OFFSET
import com.nrojiani.drone.utils.extensions.permutations
import java.time.ZonedDateTime
import java.util.ArrayDeque
import java.util.Queue

/**
 * This delivery scheduler calculates the NPS for every permutation of the list of orders,
 * and generates the scheduling order that maximizes NPS.
 *
 * Since permutation generation has time complexity of `O(n!)`, using this scheduler would
 * make the most sense if there is a relatively small number of orders to schedule.
 */
class PermutationOptimizingDeliveryScheduler(
    private val operatingHours: TimeInterval,
    private val delegate: SchedulingDelegate,
    private val deliveriesProcessor: DeliveriesProcessor
) : DeliveryScheduler {

    /**
     * Orders that are queued for scheduling.
     */
    private val queuedOrders: Queue<PendingDeliveryOrder> = ArrayDeque()

    override fun scheduleDeliveries(
        pendingOrders: List<PendingDeliveryOrder>
    ): List<DroneDelivery> {
        if (pendingOrders.isEmpty()) return emptyList()

        val permutations: Set<List<PendingDeliveryOrder>> = pendingOrders.permutations()
        val scheduledDeliveries: Map<List<PendingDeliveryOrder>, List<DroneDelivery>> = permutations.associateBy(
            keySelector = { it },
            valueTransform = { permutation ->
                scheduleAll(permutation)
            }
        )
        val deliveriesToNpsMap = scheduledDeliveries.map { (perm, deliveries) ->
            deliveries to deliveriesProcessor.predictedRecommendationsFor(deliveries)
                .run(::calculateNPS)
        }

        deliveriesToNpsMap.forEach { (deliveries, nps) ->
            println("order: ${deliveries.map { it.orderWithTransitTime.orderId }} => $nps")
        }
        return deliveriesToNpsMap.maxBy { (deliveries, nps) -> nps }!!.first

        // TODO: for ties, pick one with earliest final drone return time
        }

        /** Repeatedly process until all orders are scheduled. */
        private fun scheduleAll(orders: List<PendingDeliveryOrder>): List<DroneDelivery> {
            if (orders.isEmpty()) return emptyList()

            val scheduled: MutableList<DroneDelivery> = ArrayList()
            var timeFirstOrderPlaced = orders.first().dateTimeOrderPlaced
            var deliveryStartTime = delegate.calculateFirstDeliveryStartTime(timeFirstOrderPlaced)

            queuedOrders.addAll(orders)
            do {
                val (newlyScheduled, rollovers) = delegate.schedule(
                    sortedOrders = queuedOrders,
                    startTime = deliveryStartTime
                )

                scheduled.addAll(newlyScheduled)
                queuedOrders.clear()
                queuedOrders.addAll(rollovers)
                deliveryStartTime = ZonedDateTime.of(
                    deliveryStartTime.toLocalDate().plusDays(1),
                    operatingHours.start.toLocalTime(),
                    DEFAULT_ZONE_OFFSET
                )
            } while (queuedOrders.isNotEmpty())

            return scheduled
        }
    }