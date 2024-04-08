#!/usr/bin/env kotlin

import java.io.File
import kotlin.system.exitProcess

val projectRoot = __FILE__.absolutePath.let {
    it.substring(0, it.indexOf("/OddOneOut") + "/OddOneOut".length)
}

fun main() {
    val resDir = File("$projectRoot/dictionary/src/main/res")

    val langDirs = resDir.listFiles { file -> file.isDirectory && file.name.startsWith("values") } ?: return

    val modifiedStringsFiles = "git diff --cached --name-only".runCommand()?.lines()
        ?.filter { it.contains("res/values") && it.endsWith("strings.xml") }
        ?: return

    if (modifiedStringsFiles.isEmpty()) {
        println("No modifications in string resources detected.")
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
            println("The string '$stringName' has been modified but not updated in: ${missingUpdates.joinToString(", ")}")
            inconsistenciesFound = true
        }
    }

    if (inconsistenciesFound) {
        println("String resources are not synchronized across all languages. Please make the necessary updates.")
        exitProcess(1)
    } else {
        println("All modified string resources are synchronized across languages.")
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