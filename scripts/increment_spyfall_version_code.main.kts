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

printGreen("Incrementing the version code for spyfall")

// Load the .properties file
val properties = Properties()
val reader = BufferedReader(FileReader("app_versions.properties"))
properties.load(reader)
reader.close()

// Update the value of the "spyfall.versionCode" property
val currentVersion = properties.getProperty("spyfall.versionCode").toInt()
printGreen("current version code for spyfall is $currentVersion. New version will be ${currentVersion + 1}")

properties.setProperty("spyfall.versionCode", "${currentVersion + 1}")

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

printGreen("spyfall version code successfully incremented to ${currentVersion + 1}")

