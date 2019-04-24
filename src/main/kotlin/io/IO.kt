@file:JvmName("IOUtils")

package io

import java.io.File
import java.io.FileNotFoundException

/**
 * Read a file and return a list of String representing the lines in the file.
 * @param filePath The file's absolute path
 * @throws FileNotFoundException if the attempt to open file at [filePath] fails.
 */
fun readFileLines(filePath: String): List<String> = File(filePath).readLines()
