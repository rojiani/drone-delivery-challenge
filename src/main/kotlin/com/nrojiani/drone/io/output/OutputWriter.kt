package com.nrojiani.drone.io.output

import com.nrojiani.drone.io.OUTPUT_DIR
import com.nrojiani.drone.model.delivery.DroneDelivery
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OutputWriter(
    private val scheduledDeliveries: List<DroneDelivery>,
    private val nps: Double
) {
    fun writeOutputFile() {
        val file = File(outputFilepath)

        println("output filepath: $outputFilepath")

        val ordersContent: String = scheduledDeliveries.map {
            "${it.order.orderId} ${it.timeDroneDeparted.formattedTime}"
        }.joinToString("\n")

        // TODO
         val npsLine = "NPS ${nps.formatToNDecimalPlaces(2)}"
         val fileText = "${ordersContent}\n$npsLine"
         file.writeText(fileText)
    }

    private val outputFilepath: String
        get() = "$OUTPUT_DIR/output-${formattedTimestamp}"

    private val formattedTimestamp: String
        get() = LocalDateTime.now().formattedTime

    private val LocalDateTime.formattedTime: String
        get() = format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    /**
     * Format the [Double] as a String with [n] decimal places.
     */
    private fun Double.formatToNDecimalPlaces(n: Int): String = "%.${n}f".format(this)
}