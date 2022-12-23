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

if (args.size < 1 || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed("""
        This scipt increments the version code for the supplied application name 
        
        Usage: ./increment_version_code.main.kts [option] 
        option: "spyfall" ,"werewolf" ...
    """.trimIndent())
    throw Exception("See Message Above")
}

val appName = args[0]

printGreen("Incrementing the version code for $appName")

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app_versions.properties"))
properties.load(reader)
reader.close()

// Update the value of the "versionCode" property
val currentVersion = properties.getProperty("$appName.versionCode").toInt()
printGreen("current version code for $appName is $currentVersion. New version will be ${currentVersion + 1}")

properties.setProperty("$appName.versionCode", "${currentVersion + 1}")

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

printGreen("$appName version code successfully incremented to ${currentVersion + 1}")

