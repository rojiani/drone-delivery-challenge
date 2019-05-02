package com.nrojiani.drone.io.output

import com.nrojiani.drone.io.OUTPUT_DIR
import com.nrojiani.drone.model.delivery.DroneDelivery
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

        println("Results written to output file at:\n$outputFilepath")

        val ordersContent: String = scheduledDeliveries.joinToString("\n") {
            "${it.orderWithTransitTime.orderId} ${it.timeDroneDeparted.formattedTime}"
        }

        val npsLine = "NPS ${nps.formatToNDecimalPlaces(2)}"
        val fileText = "$ordersContent\n$npsLine"
        file.writeText(fileText)

        println("Preview of Output File:\n----(begin)----")
        val outputText = File(outputFilepath).readText()
        println(outputText)
        println("----(end)---\n")
    }

    private val outputFilepath: String
        get() = "$OUTPUT_DIR/output-$formattedTimestamp"

    private val formattedTimestamp: String
        get() = ZonedDateTime.now().formattedTime

    private val ZonedDateTime.formattedTime: String
        get() = format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    /**
     * Format the [Double] as a String with [n] decimal places.
     */
    private fun Double.formatToNDecimalPlaces(n: Int): String = "%.${n}f".format(this)
}