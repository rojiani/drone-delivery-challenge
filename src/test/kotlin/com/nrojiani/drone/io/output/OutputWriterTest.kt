package com.nrojiani.drone.io.output

import com.nrojiani.drone.io.OUTPUT_DIR
import com.nrojiani.drone.model.delivery.DroneDelivery
import com.nrojiani.drone.model.time.UTC_ZONE_ID
import com.nrojiani.drone.testutils.OrderData
import com.nrojiani.drone.testutils.TODAY
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OutputWriterTest {

    private val scheduledDeliveries: List<DroneDelivery> = listOf(
        DroneDelivery(OrderData.PENDING_ORDER_2, ZonedDateTime.of(TODAY, LocalTime.parse("06:00:00"), UTC_ZONE_ID)),
        DroneDelivery(OrderData.PENDING_ORDER_1, ZonedDateTime.of(TODAY, LocalTime.parse("06:07:12"), UTC_ZONE_ID)),
        DroneDelivery(OrderData.PENDING_ORDER_4, ZonedDateTime.of(TODAY, LocalTime.parse("06:31:22"), UTC_ZONE_ID)),
        DroneDelivery(OrderData.PENDING_ORDER_3, ZonedDateTime.of(TODAY, LocalTime.parse("06:55:32"), UTC_ZONE_ID))
    )

    private val nps: Double = 75.0
    private val outputDirPath = OUTPUT_DIR
    private lateinit var outputWriter: OutputWriter
    private lateinit var outputDir: File

    @Before
    fun setUp() {
        outputWriter = OutputWriter(scheduledDeliveries, nps)
        outputDir = File(outputDirPath)
    }

    @After
    fun tearDown() {
        // delete all files
        outputDir.list().forEach { fp ->
            val filePath: Path = Paths.get("$outputDirPath/$fp")
            Files.delete(filePath)
        }
    }

    @Test
    fun `writeOutputFile creates a new file`() {
        val fileCountBefore = outputDir.list().size
        outputWriter.writeOutputFile()
        assertEquals(outputDir.list().size, fileCountBefore + 1)
    }

    @Test
    fun `writeOutputFile new file has expected name format`() {
        outputWriter.writeOutputFile()

        val outputFileName = outputDir.list().firstOrNull()
        assertNotNull(outputFileName)
        // output-HH:MM:SS
        val regex = Regex("""output-\d{2}:\d{2}:\d{2}""")
        assertTrue(regex.matches(outputFileName))
    }

    @Test
    fun `writeOutputFile writes one line for each delivery`() {
        outputWriter.writeOutputFile()

        val outputFileName = outputDir.list().firstOrNull()
        assertNotNull(outputFileName)
        val file = File("$outputDirPath/$outputFileName")
        val lines = file.readLines()
        val deliveryLines = lines.dropLast(1) // drop NPS line
        assertEquals(scheduledDeliveries.size, deliveryLines.size)
    }

    @Test
    fun `writeOutputFile writes NPS`() {
        outputWriter.writeOutputFile()

        val outputFileName = outputDir.list().firstOrNull()
        assertNotNull(outputFileName)
        val file = File("$outputDirPath/$outputFileName")
        val lines = file.readLines()
        val npsLine = lines.last()
        assertEquals(scheduledDeliveries.size + 1, lines.size)
        assertTrue(
            Regex("""NPS \d{2}.\d{2}""").matches(npsLine)
        )
    }
}