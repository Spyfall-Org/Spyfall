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
        This script sets the version code for all of the apps. 
        Ci uses to distinguish app builds per workflow run
        
        Usage: ./set_version_code.main.kts [versionCode] 
        appName: "spyfall" ,"werewolf", ...
        versionCode: 500, 543, ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val inputVersionCode = args[0]

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app.properties"))
properties.load(reader)
reader.close()

val appDirectories: Array<File> = File("apps").listFiles { child -> child.isDirectory } ?: arrayOf()

appDirectories.map { it.name }.forEach { appName ->
    properties.setProperty("$appName.versionCode", inputVersionCode)
}

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
    # To learn more you can read the documentation here: https://spyfall-org.github.io/how-to/release/
    
""".trimIndent())
writer.newLine()
properties.store(writer, null)
writer.close()