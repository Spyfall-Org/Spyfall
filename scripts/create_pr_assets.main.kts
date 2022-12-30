#!/usr/bin/env kotlin

@file:Import("util/GithubActionsUtil.main.kts")

import java.io.BufferedReader
import java.io.File
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

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    @Suppress("MaxLineLength")
    printGreen(
        """
               This script creates the apks needed for PRs and writes the paths of those assets to the env file passed 
               in. Release assets are unsigned unless this is being ran locally. In which case they are signed
               with a debug signing config. 
               
               Usage: ./create_pr_assets.main.kts [ENV_FILE]
               ENV_FILE: The env file to read from and output to           
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

fun main() {
    val envFile = File(args.get(0))
    val isSpyfallRelease = envFile.getEnvValue("isSpyfallReleasePR")?.toBoolean ?: false
    val isWerewolfRelease = envFile.getEnvValue("isWerewolfReleasePR")?.toBoolean ?: false

    val isCIBuild = System.getenv("CI") == "true"

    val spyfallVersionName = getAppVersionName("spyfall")
    val werewolfVersionName = getAppVersionName("werewolf")

    printGreen("Assembling the entire project")
    runGradleCommand("assembleDebug")

    renameSpyfallDebugAssets(spyfallVersionName, envFile)
    renameWerewolfDebugAssets(werewolfVersionName, envFile)

    if (isSpyfallRelease) {
        runGradleCommand(":apps:spyfall:bundleRelease")
        runGradleCommand(":apps:spyfall:assembleRelease")
        renameSpyfallReleaseAssets(spyfallVersionName, envFile, isCIBuild)
    }

    if (isWerewolfRelease) {
        runGradleCommand(":apps:werewolf:bundleRelease")
        runGradleCommand(":apps:werewolf:assembleRelease")
        renameWerewolfReleaseAssets(werewolfVersionName, envFile, isCIBuild)

    }
}

fun renameSpyfallReleaseAssets(
    spyfallVersionName: String,
    envFile: File,
    isCIBuild: Boolean
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/release"),
        name = "spyfall-release-v$spyfallVersionName-$signingSuffix.apk",
        outputName = "spyfallReleaseApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findAabFile("apps/spyfall/build/outputs/bundle/release"),
        name = "spyfall-release-v$spyfallVersionName-$signingSuffix.aab",
        outputName = "spyfallReleaseAabPath",
        envFile = envFile
    )
}

fun renameWerewolfReleaseAssets(
    werewolfVersionName: String,
    envFile: File,
    isCIBuild: Boolean
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/release"),
        name = "werewolf-release-v$werewolfVersionName-$signingSuffix.apk",
        outputName = "werewolfReleaseApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findAabFile("apps/werewolf/build/outputs/bundle/release"),
        name = "werewolf-release-v$werewolfVersionName-$signingSuffix.aab",
        outputName = "werewolfReleaseAabPath",
        envFile = envFile
    )

}

fun renameWerewolfDebugAssets(werewolfVersionName: String, envFile: File) {
    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/debug"),
        name = "werewolf-debug-v$werewolfVersionName.apk",
        outputName = "werewolfDebugApkPath",
        envFile = envFile
    )
}

fun renameSpyfallDebugAssets(spyfallVersionName: String, envFile: File) {

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/debug"),
        name = "spyfall-debug-v$spyfallVersionName.apk",
        outputName = "spyfallDebugApkPath",
        envFile = envFile
    )
}

fun getAppVersionName(app: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$app.versionName").toString()
}


fun renameAsset(defaultPath: String, name: String, outputName: String, envFile: File) {
    val apkFile = File(defaultPath)
    val renamedFile = File(apkFile.parent, name)
    val didRename = apkFile.renameTo(renamedFile)
    val finalPath = if (didRename) renamedFile.absolutePath else apkFile.absolutePath

    val writer = FileWriter(envFile, true)
    writer.write("$outputName=$finalPath")
    writer.write("\n")
    writer.close()
}

fun findApkFile(parentDirectoryPath: String): String {
    val parentDirectory = File(parentDirectoryPath)
    val apkFiles = parentDirectory.listFiles { file -> file.name.endsWith(".apk") }
    @Suppress("TooGenericExceptionThrown")
    return apkFiles
        ?.firstOrNull()
        ?.absolutePath ?: throw Exception("No apk file found in directory $parentDirectoryPath")
}

fun findAabFile(parentDirectoryPath: String): String {
    val parentDirectory = File(parentDirectoryPath)
    val apkFiles = parentDirectory.listFiles { file -> file.name.endsWith(".aab") }
    @Suppress("TooGenericExceptionThrown")
    return apkFiles
        ?.firstOrNull()
        ?.absolutePath ?: throw Exception("No aab file found in directory $parentDirectoryPath")
}

fun runGradleCommand(command: String) {
    val result = ProcessBuilder("./gradlew", "$command")
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()

    @Suppress("TooGenericExceptionThrown")
    if (result != 0) {
        printRed("Failed to run $command")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }
}

main()
