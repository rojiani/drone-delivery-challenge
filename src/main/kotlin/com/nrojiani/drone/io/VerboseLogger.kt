package com.nrojiani.drone.io

import com.nrojiani.drone.cli.CommandLineArguments
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.utils.extensions.formatToNDecimalPlaces
import com.nrojiani.drone.utils.extensions.formatted

/**
 * Logs data only if [verboseModeEnabled] is true.
 */
class VerboseLogger(private val verboseModeEnabled: Boolean) {

    fun logArgs(args: Array<String>, parsedArgs: CommandLineArguments) {
        if (!verboseModeEnabled) return
        val output = buildString {
            append("\nargs: ${args.contentToString()}:\n")
            append("\tVerbose Mode (--verbose): On\n")
            append("\tInput filepath (--input): ${parsedArgs.inputFilepath}\n")
            append("\tExit on invalid input (--exitOnInvalidInput): ${parsedArgs.exitIfInvalid}\n")
            append("\tDeliveryScheduler (--scheduler): ${parsedArgs.schedulerName}\n")
        }
        println(output)
    }

    /**
     * Print the [message].
     */
    fun logMessage(message: String, newline: Boolean = false) {
        if (!verboseModeEnabled) return
        println(message)
        if (newline) println()
    }

    /**
     * Print the [message] if the [predicate] is true.
     */
    fun logMessageIf(message: String, newline: Boolean = false, predicate: () -> Boolean) {
        if (predicate()) logMessage(message, newline)
    }

    fun <T> logIterable(
        elements: Iterable<T>,
        label: String,
        newline: Boolean = true
    ) {
        if (!verboseModeEnabled) return
        println(label)
        elements.forEach {
            println(it)
        }
        if (newline) println()
    }

    fun <T> logIterableIf(
        elements: Iterable<T>,
        label: String,
        newline: Boolean = false,
        predicate: () -> Boolean
    ) {
        if (predicate()) logIterable(elements, label, newline)
    }

    fun <T, K> logIterableKeys(
        elements: Iterable<T>,
        label: String,
        keyLabel: String,
        newline: Boolean = true,
        keySelector: (T) -> K
    ) {
        if (!verboseModeEnabled) return
        println(label)
        elements.forEach {
            val beforeKey = "\t$keyLabel${if (keyLabel.isNotBlank()) ": " else ""}"
            println("$beforeKey${keySelector(it)}")
        }
        if (newline) println()
    }

    fun <T, K> logIterableKeysIf(
        elements: Iterable<T>,
        label: String,
        keyLabel: String,
        keySelector: (T) -> K,
        newline: Boolean = true,
        predicate: () -> Boolean
    ) {
        if (predicate()) logIterableKeys(elements, label, keyLabel, newline, keySelector)
    }

    fun <T, K> logIterableAndKeys(
        elements: Iterable<T>,
        elementsHeading: String,
        keysHeading: String,
        keyLabel: String,
        newline: Boolean = true,
        keySelector: (T) -> K
    ) {
        logIterable(elements, elementsHeading, false)
        logIterableKeys(elements, keysHeading, keyLabel, newline, keySelector)
    }

    /** Display delivery times as a table */
    fun logDeliveryTimes(deliveries: List<DroneDelivery>) {
        if (!verboseModeEnabled) return
        logIterableKeys(
            elements = deliveries,
            label = "Delivery Times:\n" +
                    "\tid:       | Placed               | Drone Departed       | Delivered            | Returned ",
            keyLabel = ""
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

    /**
     * Print a readout of NPS calculations for each permutation if verbose mode enabled.
     * Example:
     * ```
     * [WM001, WM002, WM003, WM004] => 75.0
     * [WM003, WM002, WM004, WM001] => 25.0
     * [WM001, WM004, WM002, WM003] => 75.0
     * ...
     * ```
     */
    fun logDeliveryOrderWithNPS(deliveriesToNpsMap: Map<List<DroneDelivery>, Double>) {
        if (!verboseModeEnabled) return
        println("Delivery Sequence => NPS")
        deliveriesToNpsMap.forEach { (deliveries, nps) ->
            println("${deliveries.map { it.orderWithTransitTime.orderId }} => $nps")
        }
        println()
    }

    fun logOptimalSequencesAndEndReturnTime(
        maxNps: Double,
        maxSequences: Set<List<DroneDelivery>>,
        maxNpsSequence: List<DroneDelivery>
    ) {
        if (!verboseModeEnabled) return
        println("Max NPS: ${maxNps.formatToNDecimalPlaces(2)}")
        println("Sequences with Max NPS:")
        maxSequences.forEach { seq ->
            val finalDroneReturnTime = seq.last().timeDroneReturned
            println("Delivery sequence: ${seq.map { it.orderWithTransitTime.orderId }} | Drone Return Time: $finalDroneReturnTime")
        }
        println(
            "\nDelivery Sequence with max NPS & earliest completion time:\n" +
                    "${maxNpsSequence.map { it.orderWithTransitTime.orderId }}" +
                    " (final delivery time: ${maxNpsSequence.last().timeOrderDelivered})\n"
        )
    }
}