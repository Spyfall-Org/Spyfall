#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
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

@Suppress("ComplexCondition", "MagicNumber")
if (args.size < 2 || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed("""
        This script sets env variables for the release version and type
        
        usage: ./set_env_release_version.main.kts [branch_name] [env_file]
        [branch_name] - branch that triggered the workflow using this script
        [env_file] - env file used to store output of this script 
        
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("UseCheckOrError", "ThrowsCount")
fun main() {
    val branchName = args[0]
    val envFile = File(args[1])

    val isRelease = branchName.contains("release")
    val isWerewolfRelease = branchName.contains("werewolf") && isRelease
    val isSpyfallRelease = branchName.contains("spyfall") && isRelease

    if (!isRelease) return

    val vXyzMatcher = "v\\d+\\.\\d+\\.\\d+".toRegex()
    val vXyMatcher = "v\\d+\\.\\d+".toRegex()

    val branchVersion = (vXyzMatcher.find(branchName) ?: vXyMatcher.find(branchName))?.value ?: throw
    IllegalStateException(" branch detected to be a release but could not extract the version.")

    val appVersionName = when {
        isSpyfallRelease -> getVersionName("spyfall")
        isWerewolfRelease -> getVersionName("werewolf")
        else -> throw IllegalStateException("""
            Got a release branch that does not match spyfall or werewolf
        """.trimIndent())
    }

    if ( !branchVersion.contains(appVersionName) ) {
        throw IllegalStateException("""
            Branch name lists version as $branchVersion which does not contain the apps version $appVersionName.
        """.trimIndent())
    }

    envFile.writer().let {
        it.write("isWerewolfReleasePR=$isWerewolfRelease")
        it.write("isSpyfallReleasePR=$isSpyfallRelease")
        it.write("releaseVersion=branchVersion$")
        it.close()
    }
}

fun getVersionName(appName: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app_versions.properties"))
    properties.load(reader)
    reader.close()

    return  properties.getProperty("$appName.versionName").toString()
}

main()
