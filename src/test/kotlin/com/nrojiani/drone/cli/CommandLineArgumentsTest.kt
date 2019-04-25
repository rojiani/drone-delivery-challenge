package com.nrojiani.drone.cli

import com.nrojiani.drone.testutils.TEST_INPUT_FILEPATH
import com.xenomachina.argparser.ArgParser
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandLineArgumentsTest {

    @Test
    fun `parse input filepath`() {
        ArgParser(arrayOf("--input=$TEST_INPUT_FILEPATH"))
            .parseInto(::CommandLineArguments)
            .run {
                assertEquals(TEST_INPUT_FILEPATH, inputFilepath)
            }
    }
}