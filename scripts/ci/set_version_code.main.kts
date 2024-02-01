#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
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

@Suppress("ComplexCondition")
if (args.isEmpty() || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed("""
        This script sets the version code. 
        Ci uses to distinguish app builds per workflow run
        
        Usage: ./set_version_code.main.kts [run number]
        versionCode: 500, 543, ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val runNumber = args[0]

printGreen("Setting version code to $runNumber")
// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app.properties"))
properties.load(reader)
reader.close()

properties.setProperty("versionCode", "$runNumber")

// Save the .properties file
val writer = BufferedWriter(FileWriter("app.properties"))
writer.write("""
    # These properties are referenced in: \n
    # build-logic/convention/AndroidApplicationConventionPlugin.kt  \n
    # .github/workflows/create-release..
    # This is to make finding.updating the app properties with ci much easier
    # These values are set by our CI exclusively
    # The version code matches the CI build number, this helps us distinguish between multiple builds of the same
    # version name
    # The version name is set by the set_version_name script which is triggered by a github action    
""".trimIndent())
writer.newLine()
properties.store(writer, null)
writer.close()
