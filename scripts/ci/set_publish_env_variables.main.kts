#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

@Suppress("MagicNumber")
fun main() {
    val envFile = File(args[0])
    val tagName = args[1]

    val packageName = getPackageName(getAppName(tagName))

    FileWriter(envFile, true).apply {
        write("packageName=$packageName")
        appendLine()
        close()
     }
}

@Suppress("UseCheckOrError")
fun getAppName(tagName: String) = when {
    tagName.contains("oddoneout") -> "oddoneout"
    else -> throw IllegalStateException(
        "Could not extract app name from tag $tagName. Please make sure tag name are formatted correctly."
    )
}

fun getPackageName(appName: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$appName.packageName")
}

main()
