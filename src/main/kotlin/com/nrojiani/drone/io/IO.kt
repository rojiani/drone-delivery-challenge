@file:JvmName("IOUtils")

package com.nrojiani.drone.io

import java.io.File
import java.io.FileNotFoundException

/**
 * Read a file and return a list of String representing the lines in the file.
 * @param filePath The file's absolute path
 * @throws FileNotFoundException if the attempt to open file at [filePath] fails.
 */
fun readFileLines(filePath: String): List<String> = File(filePath).readLines()

const val PROJECT_DIR = "/Users/nrojiani/IdeaProjects/drone-delivery-challenge"
const val RESOURCES_DIR = "$PROJECT_DIR/src/main/resources"
const val TEST_INPUT_DIR = "$RESOURCES_DIR/input"
/** Directory where output files are written */
const val OUTPUT_DIR = "$RESOURCES_DIR/output"