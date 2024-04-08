#!/usr/bin/env kotlin

import java.io.File
import kotlin.system.exitProcess

val red = "\u001b[31m"
val green = "\u001b[32m"
val cyan = "\u001b[36m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printCyan(text: String) {
    println(cyan + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}


val projectRoot = __FILE__.absolutePath.let {
    it.substring(0, it.indexOf("/OddOneOut") + "/OddOneOut".length)
}

fun main() {
    printCyan("\n************Checking that any changed string have been updated in other languages*******\n")

    val resDir = File("$projectRoot/dictionary/src/main/res")

    if (!resDir.exists()) {
        printRed("Res directory not found. ${resDir.absolutePath}")
        exitProcess(1)
    }

    println("All children of res dir: ${resDir.listFiles().joinToString(", ") { it.name }}")

    val langDirs = resDir.listFiles { file -> file.isDirectory && file.name.startsWith("values") }

    println("Languages found: ${langDirs.joinToString { it.name }}")

    val modifiedStringsFiles = (
            "git diff --cached --name-only".runCommand()?.lines().orEmpty() +
            "git diff --name-only".runCommand()?.lines().orEmpty()
            ).distinct()
        .filter { it.contains("res/values") && it.endsWith("strings.xml") }


    if (modifiedStringsFiles.isEmpty()) {
        printGreen("No modifications in string resources detected.\n")
        return
    }

    val modifiedStrings = mutableMapOf<String, MutableList<String>>()
    modifiedStringsFiles.forEach { filePath ->
        val lang = File(filePath).parentFile.name
        "git diff --cached $filePath".runCommand()?.lines()
            ?.filter { it.startsWith("+") && it.contains("<string name=") }
            ?.forEach { line ->
                Regex("name=\"([^\"]+)\"").find(line)?.groups?.get(1)?.value?.let { stringName ->
                    modifiedStrings.getOrPut(stringName) { mutableListOf() }.add(lang)
                }
            }
    }

    var inconsistenciesFound = false
    modifiedStrings.forEach { (stringName, langs) ->
        val missingUpdates = langDirs.map { it.name }.filterNot { langs.contains(it) }
        if (missingUpdates.isNotEmpty()) {
            println("'$stringName' has been modified but not updated in: ${missingUpdates.joinToString(", ")}")
            inconsistenciesFound = true
        }
    }

    if (inconsistenciesFound) {
        printRed("String resources are not synchronized across all languages. Please make the necessary updates.")
        exitProcess(1)
    } else {
        printGreen("All modified string resources are synchronized across languages.")
    }
}

fun String.runCommand(workingDir: File = File(".")): String? =
    try {
        ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .apply { waitFor() }
            .inputStream.bufferedReader().readText()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

main()