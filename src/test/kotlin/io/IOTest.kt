package io

import org.junit.Test

import org.junit.Assert.*
import java.io.FileNotFoundException
import kotlin.test.assertFailsWith

class IOTest {

    companion object {
        private const val PROJECT_DIR = "/Users/nrojiani/IdeaProjects/drone-delivery-challenge"
        private const val TEST_INPUT_DIR = "$PROJECT_DIR/src/main/resources/input"
        const val TEST_INPUT_1_PATH = "$TEST_INPUT_DIR/test-input-1"
        const val TEST_INPUT_DOES_NOT_EXIST = "$TEST_INPUT_DIR/test-input-non-existent"
    }

    @Test
    fun readFileLines() {
        assertEquals(
            listOf(
                "WM001 N11W5 05:11:50",
                "WM002 S3E2 05:11:55",
                "WM003 N7E50 05:31:50",
                "WM004 N11E5 06:11:50"
            ), readFileLines(TEST_INPUT_1_PATH)
        )
    }

    @Test
    fun readFileLines_withInvalidPath_throwsException() {
        assertFailsWith<FileNotFoundException> {
            readFileLines(TEST_INPUT_DOES_NOT_EXIST)
        }
    }
}