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
if (args.isEmpty() || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed("""
        This script sets the version code for the supplied application name by either taking input to set it
        or automatically incrementing the version code by 1
        
        Usage: ./set_version_code.main.kts [appName] [versionCode] 
        appName: "spyfall" ,"werewolf", ...
        versionCode: 500, 543, ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val appName = args[0]
val inputVersionCode = args.getOrNull(1)

if (inputVersionCode == null) {
    printGreen("Incrementing the version code for $appName")
} else {
    printGreen("setting the version code for $appName to inputted version of $inputVersionCode")
}

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app_versions.properties"))
properties.load(reader)
reader.close()

// Update the value of the "versionCode" property
val currentVersion = properties.getProperty("$appName.versionCode").toInt()
val newVersionCode = inputVersionCode ?: currentVersion

printGreen("""
    current version code for $appName is $currentVersion. 
    New version will be ${inputVersionCode ?: currentVersion + 1}""".trimIndent()
)

properties.setProperty("$appName.versionCode", "${inputVersionCode ?: currentVersion + 1}")

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

printGreen("$appName version code successfully incremented to ${inputVersionCode ?: currentVersion + 1}")

