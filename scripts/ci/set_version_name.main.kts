#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.BufferedWriter
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

if (args.size < 2 || args[0] == "-h" || args[0].contains("help")) {
    printRed("""
        This script sets the version name for the supplied application name 
        
        Usage: ./set_version_name.main.kts [appName] [versionName] 
        appName: "spyfall" ,"werewolf", ...
        versionName: "1.2.4", "1.5.19", ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val appName = args[0].trim()
val newVersionName = args[1].trim()

printGreen("Setting the version name for $appName")

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app.properties"))
properties.load(reader)
reader.close()

// Update the value of the "versionName" property
val currentVersionName = properties.getProperty("$appName.versionName").toString()
printGreen("current version name for $appName is $currentVersionName")

properties.setProperty("$appName.versionName", newVersionName)

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

printGreen("$appName version name successfully set to $newVersionName")
