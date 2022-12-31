#!/usr/bin/env kotlin


import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

@Suppress("MagicNumber")
fun main() {
    val envFile = File(args[0])
    val releaseName = args[1]
    val manualInputTagName = args[2]
    val triggeredTagName = args[3]
    val packageName = getPackageName(getAppName(releaseName))

    val tagName = triggeredTagName.ifEmpty { manualInputTagName }

    FileWriter(envFile, true).apply {
        write("packageName=$packageName")
        appendLine()
        write("releaseTagName=$tagName")
        appendLine()
        close()
     }
}

@Suppress("UseCheckOrError")
fun getAppName(releaseName: String) = when {
    releaseName.contains("spyfall") -> "spyfall"
    releaseName.contains("werewolf") -> "werewolf"
    else -> throw IllegalStateException("Could not extract app name from release $releaseName")
}

fun getPackageName(appName: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$appName.packageName")
}

main()
