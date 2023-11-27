#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.regex.Pattern

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

@Suppress("ReturnCount")
fun main() {
    if (checkForHelpCall()) return

    val moduleType = args.getOrNull(0) ?: run {
        print("Enter module type (\"feature\" or \"library\"): ")
        readln().lowercase()
    }

    if (moduleType != "feature" && moduleType != "library") {
        printRed("Error: Invalid module type. Must be \"feature\" or \"library\"")
        return
    }

    val fullModuleName = args.getOrNull(1) ?: run {
        print(
            """
            Enter the module name in camelCase. or "q" to quit
            If this module is a sub module enter the name in the form "parentModule:subModule": 
            """.trimIndent()
        )
        readln()
    }

    if (fullModuleName == "q") {
        printGreen("Quitting...")
        return
    }

    val isManualInternal = fullModuleName.split(":").getOrNull(1) == "internal"

    generateModule(fullModuleName, moduleType, isInternal = isManualInternal)
    updateAppGradleFile(moduleType = moduleType, isInternal = isManualInternal, moduleName = fullModuleName)

    val isSubModule = fullModuleName.contains(":")

    if (!isSubModule && !isManualInternal) {
        updateAppGradleFile(moduleType = moduleType, isInternal = true, moduleName = fullModuleName)
        generateModule("$fullModuleName:internal", moduleType, isInternal = true)
    }
}

fun updateSettingGradleFile(baseDir: String, moduleName: String, parentModule: String?) {
    val includeLine = "include(\"$baseDir:${if (parentModule != null) "$parentModule:" else "" }$moduleName\")"

    val includePattern = Pattern.compile("include\\(\"[^\\)]+\"\\)")
    val settingsFile = File("settings.gradle.kts")
    val settingsLines = settingsFile.readLines().toMutableList()

    if (!settingsLines.contains(includeLine)) {
        settingsLines += includeLine
    }

    val indexOfFirstInclude = settingsLines.indexOfFirst { it.matches(includePattern.toRegex()) }
    val indexOfLastInclude = settingsLines.indexOfLast { it.matches(includePattern.toRegex()) }

    settingsLines.subList(indexOfFirstInclude, indexOfLastInclude).sortBy {
        it.substringAfter("include(").substringBeforeLast(")")
    }
    settingsFile.writeText(settingsLines.joinToString("\n"))
}

fun createPackage(directory: String, featureName: String?) {
    val packageString = directory.replace("/", ".").lowercase()
    val packageName = "com.dangerfield.$packageString"

    val mainDir = File("$directory/src/main/java/$packageName")
    mainDir.mkdirs()

    val testDir = File("$directory/src/test/java/$packageName")
    testDir.mkdirs()

    val navigationFile = File("$directory/src/main/Navigation.kt")

    if (featureName == null) {
        navigationFile.delete()
    } else {
        val lines = navigationFile.readLines()
        val newLines = lines.map { it.replace("example", featureName ) }
        navigationFile.writeText(newLines.joinToString("\n"))
        navigationFile.renameTo(File("$mainDir/Navigation.kt"))
    }
}

fun createDirectory(baseDir: String, moduleName: String, parentModule: String?): String {
    val exampleDir = "scripts/exampleModule"
    val newDir = "$baseDir/${if (parentModule != null) "$parentModule/" else ""}$moduleName"

    val newDirFinal = File(newDir).apply { parentFile.mkdir() }

    File(exampleDir).copyRecursively(newDirFinal, overwrite = true)


    return newDir
}

fun updateAppGradleFile(moduleType: String, moduleName: String, isInternal: Boolean) {
    val appBuildGradleFile = File("app/build.gradle.kts")

    val internalAddition = if (isInternal) ".internal" else ""
    val moduleParentName = if (moduleType == "library") "libraries" else "features"
    val moduleNameCleaned = moduleName.replace(":", ".").lowercase()
    val lineToAdd = "\timplementation(projects.$moduleParentName.$moduleNameCleaned$internalAddition)"

    val lines = appBuildGradleFile.readLines().toMutableList()

    val indexToAdd = lines.indexOfFirst { it.contains("STOP PROJECT MODULES") }

    lines.add(indexToAdd, lineToAdd)

    appBuildGradleFile.writeText(lines.joinToString("\n"))

}

@Suppress("LongMethod")
fun updateGradleBuildFile(moduleType: String, newDir: String, isInternal: Boolean, parentModule: String?) {
    val buildFile = when {
        isInternal -> {
            if (moduleType == "library") {
                val currentBuildFile = File("$newDir/internalLibrarybuild.gradle.kts")
                val newBuildFile = File("$newDir/build.gradle.kts")
                val filesToDelete = listOf(
                    File("$newDir/librarybuild.gradle.kts"),
                    File("$newDir/featurebuild.gradle.kts"),
                    File("$newDir/internalFeaturebuild.gradle.kts")
                )

                currentBuildFile.renameTo(newBuildFile)
                filesToDelete.forEach { it.delete() }
                newBuildFile
            } else {
                val currentBuildFile = File("$newDir/internalFeaturebuild.gradle.kts")
                val newBuildFile = File("$newDir/build.gradle.kts")
                val filesToDelete = listOf(
                    File("$newDir/librarybuild.gradle.kts"),
                    File("$newDir/featurebuild.gradle.kts"),
                    File("$newDir/internalLibrarybuild.gradle.kts")
                )

                currentBuildFile.renameTo(newBuildFile)
                filesToDelete.forEach { it.delete() }
                newBuildFile
            }
        }
        moduleType == "library" -> {
            val currentBuildFile = File("$newDir/librarybuild.gradle.kts")
            val newBuildFile = File("$newDir/build.gradle.kts")
            val filesToDelete = listOf(
                File("$newDir/featurebuild.gradle.kts"),
                File("$newDir/internalFeaturebuild.gradle.kts"),
                File("$newDir/internalLibrarybuild.gradle.kts")
            )

            currentBuildFile.renameTo(newBuildFile)
            filesToDelete.forEach { it.delete() }
            newBuildFile
        }
        else -> {
            val currentBuildFile = File("$newDir/featurebuild.gradle.kts")
            val newBuildFile = File("$newDir/build.gradle.kts")
            val filesToDelete = listOf(
                File("$newDir/librarybuild.gradle.kts"),
                File("$newDir/internalbuild.gradle.kts"),
                File("$newDir/internalFeaturebuild.gradle.kts"),
                File("$newDir/internalLibrarybuild.gradle.kts")
            )

            currentBuildFile.renameTo(newBuildFile)
            filesToDelete.forEach { it.delete() }
            newBuildFile
        }
    }

    val reader = BufferedReader(FileReader(buildFile))
    val modifiedLines = mutableListOf<String>()

    var line = reader.readLine()
    while (line != null) {
        if (line.contains("implementation") && line.contains("example")) {
            line = if (parentModule == null) "" else line.replace("example", parentModule)
        }
        if (line.contains("namespace = \"com.dangerfield.example\"")) {
            val newNamespace = "com.dangerfield.spyfall.${newDir.replace("/",".").lowercase()}"
            line = line.replace("com.dangerfield.example", newNamespace)
        }
        modifiedLines.add(line)
        line = reader.readLine()
    }
    reader.close()

    // Write the modified lines back to the file
    val writer = BufferedWriter(FileWriter(buildFile))
    for (modifiedLine in modifiedLines) {
        writer.write(modifiedLine)
        writer.newLine()
    }
    writer.close()
}

fun checkForHelpCall(): Boolean {
    val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
    if (isHelpCall) {
        @Suppress("MaxLineLength")
        printGreen(
            """
               This script creates a new module according to our module structure. 
               
               
               Usage: ./create_module.main.kts [options]
               option module-type - the type of the module to create: "library" or "feature" 
               option module-name - The camelCase name of the module to create
               
            """.trimIndent()
        )
    }

    return isHelpCall
}

fun generateModule(moduleNameLine: String, moduleType: String, isInternal: Boolean = false) {
    val (parentModule, moduleName) = moduleNameLine.split(":").let {
        if (it.size > 1) Pair(it[0], it[1]) else Pair(null, it[0])
    }

    val baseDir = if (moduleType == "library") "libraries" else "features"

    val newDir = createDirectory(baseDir, moduleName, parentModule)

    createPackage(newDir, moduleName)

    updateSettingGradleFile(baseDir, moduleName, parentModule)

    updateGradleBuildFile(
        moduleType = moduleType,
        newDir = newDir,
        isInternal = isInternal,
        parentModule = parentModule
    )

    printGreen(
        """
       Success! 
       The $moduleType module "$moduleName" was created. 
       Please make sure to update the readme.
        """.trimIndent()
    )
}

main()

