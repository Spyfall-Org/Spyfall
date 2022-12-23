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

@Suppress("ComplexCondition")
if (args.size < 2 || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed("""
        This script sets the version name for the supplied application name 
        
        Usage: ./set_version_name.main.kts [appName] [versionName] 
        appName: "spyfall" ,"werewolf", ...
        versionName: "1.2.4", "1.5.19", ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val appName = args[0]
val newVersionName = args[1]

printGreen("Setting the version name for $appName")

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app_versions.properties"))
properties.load(reader)
reader.close()

// Update the value of the "versionName" property
val currentVersionName = properties.getProperty("$appName.versionName").toString()
printGreen("current version name for $appName is $currentVersionName")

properties.setProperty("$appName.versionName", newVersionName)

// Save the .properties file
val writer = BufferedWriter(FileWriter("app_versions.properties"))
writer.write("""
    # These properties are referenced in: \n
    # build-logic/convention/AndroidApplicationConventionPlugin.kt  \n
    # .github/workflows/create-release..
    # This is to make updating the app versions with ci much easier
""".trimIndent())
writer.newLine()
properties.store(writer, null)
writer.close()

printGreen("$appName version name successfully set to $newVersionName")

