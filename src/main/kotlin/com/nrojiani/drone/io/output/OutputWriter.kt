package com.nrojiani.drone.io.output

import com.nrojiani.drone.io.OUTPUT_DIR
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.utils.extensions.formatToNDecimalPlaces
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Responsible for the writing an output file.
 */
class OutputWriter(
    private val scheduledDeliveries: List<DroneDelivery>,
    private val nps: Double
) {
    fun writeOutputFile() {
        val file = File(outputFilepath)

        println("\nResults written to output file at:\n$outputFilepath")

        val ordersContent: String = scheduledDeliveries.joinToString("\n") {
            "${it.orderWithTransitTime.orderId} ${it.timeDroneDeparted.formattedTime}"
        }

        val npsLine = "NPS ${nps.formatToNDecimalPlaces(2)}"
        val fileText = "$ordersContent\n$npsLine"
        file.writeText(fileText)

        printOutputFilePreview()
    }

    private val outputFilepath: String
        get() = "$OUTPUT_DIR/output-$formattedTimestamp"

    private val formattedTimestamp: String
        get() = ZonedDateTime.now().formattedTime

    private val ZonedDateTime.formattedTime: String
        get() = format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    private fun printOutputFilePreview() {
        println("\nPreview of Output File:\n---------------")
        val outputText = File(outputFilepath).readText()
        println(outputText)
        println("--------------\n")
    }
}