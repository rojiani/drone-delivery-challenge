package com.nrojiani.drone.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

class Hello : CliktCommand() {
    val count: Int by option(help="Number of greetings").int().default(1)

    override fun run() {
        println("run: count = $count")
    }
}

fun main(args: Array<String>) {
    println("main(args: ${args.toList()})")

    Hello().main(args)
}
