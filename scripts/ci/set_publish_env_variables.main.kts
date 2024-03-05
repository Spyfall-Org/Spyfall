#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

@Suppress("MagicNumber")
fun main() {
    val envFile = File(args[0])
    val packageName = getPackageName()

    FileWriter(envFile, true).apply {
        write("packageName=$packageName")
        appendLine()
        close()
     }
}

fun getPackageName(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("packageName")
}

main()
