package com.nrojiani.drone.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path

class Hello : CliktCommand() {
    val inputFile by argument().path(
        exists = true,
        folderOkay = false,
        readable = true
    )

    override fun run() {
        println("run: inputFile = $inputFile")
    }
}

fun main(args: Array<String>) {
    println("main(args: ${args.toList()})")

    Hello().main(args)
}
