#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths
import java.util.Properties

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}


fun main() {
    @Suppress("ComplexCondition")
    if (args.isEmpty() || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
        printRed("""
        This script sets the version string in the create release workflow. 
        
        Usage: ./rename_current_release_in_create_release_script.main.kts [version]
        version: 1.3.2, 1.4.0, ...
    """.trimIndent())

        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }

    val newVersion = args[0]
    val filePath = Paths.get(".github", "workflows", "create_release_branch.yml").toString()
    val file = File(filePath)

    if (!file.exists()) {
        printRed("The file $filePath does not exist.")
        return
    }

    val updatedContent = file.readLines().joinToString("\n") { line ->
        if (line.trimStart().startsWith("description: 'Version being released")) {
            "        description: 'Version being released (current is $newVersion)'"
        } else {
            line
        }
    }

    file.writeText(updatedContent)
    printGreen("Updated the version to $newVersion in $filePath.")
}

main()
