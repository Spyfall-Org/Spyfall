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

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    @Suppress("MaxLineLength")
    printGreen(
        """
               This script creates the apks needed for PRs and writes the paths of those assets to the env file passed in
               
               Usage: ./create_pr_assets.kts [ENV] [option1] [option2]
               ENV: The env file to output to
               Option 1: true if you want to build spyfall release
               Option 2: true if you want to build werewolf release
               
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

fun main() {
    val envFile = File(args.get(0))
    val shouldBuildSpyfallRelease = args.getOrNull(1)?.let { it.toBoolean() } ?: false
    val shouldBuildWerewolfRelease = args.getOrNull(2)?.let { it.toBoolean() } ?: false

    val spyfallVersionName = getAppVersionName("spyfall")
    val werewolfVersionName = getAppVersionName("werewolf")

    createWerewolfDebugAssets(werewolfVersionName, envFile)
    createSpyfallDebugAssets(spyfallVersionName, envFile)

    if (shouldBuildSpyfallRelease) {
        createSpyfallReleaseAssets(spyfallVersionName, envFile)
    }

    if (shouldBuildWerewolfRelease) {
        createWerewolfReleaseAssets(werewolfVersionName, envFile)
    }
}

fun getAppVersionName(app: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app_versions.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$app.versionName").toString()
}


fun makeAsset(gradleCommand: String, defaultPath: String, name: String, outputName: String, envFile: File ) {
    val result = ProcessBuilder("./gradlew", gradleCommand)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()

    if (result != 0) {
        printRed("Failed to run ./gradlew $gradleCommand")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }

    val apkFile = File(defaultPath)
    apkFile.renameTo(File(apkFile.parent, name))
    val finalPath = apkFile.absolutePath

    envFile.printWriter().use {
        it.println("$outputName=$finalPath")
    }
}

main()

fun Create_pr_assets.createSpyfallReleaseAssets(spyfallVersionName: String, envFile: File) {
    makeAsset(
        gradleCommand = ":apps:spyfall:assembleLegacyRelease",
        defaultPath = "apps/spyfall/build/outputs/apk/legacy/release/spyfall-legacy-release.apk",
        name = "spyfall-legacy-release-v$spyfallVersionName.apk",
        outputName = "spyfallLegacyReleaseApkPath",
        envFile = envFile
    )

    makeAsset(
        gradleCommand = ":apps:spyfall:bundleLegacyRelease",
        defaultPath = "apps/spyfall/build/outputs/apk/legacy/release/spyfall-legacy-release.aab",
        name = "spyfall-legacy-release-v$spyfallVersionName.aab",
        outputName = "spyfallLegacyReleaseAabPath",
        envFile = envFile
    )

    makeAsset(
        gradleCommand = ":apps:spyfall:assembleRefactorRelease",
        defaultPath = "apps/spyfall/build/outputs/apk/refactor/release/spyfall-refactor-release.apk",
        name = "spyfall-refactor-release-v$spyfallVersionName.apk",
        outputName = "spyfallRefactorReleaseApkPath",
        envFile = envFile
    )

    makeAsset(
        gradleCommand = ":apps:spyfall:bundleRefactorRelease",
        defaultPath = "apps/spyfall/build/outputs/bundle/refactor/release/spyfall-refactor-release.aab",
        name = "spyfall-refactor-release-v$spyfallVersionName.aab",
        outputName = "spyfallRefactorReleaseAabPath",
        envFile = envFile
    )
}

fun Create_pr_assets.createWerewolfDebugAssets(werewolfVersionName: String, envFile: File ) {
    makeAsset(
        gradleCommand = ":apps:werewolf:assembleDebug",
        defaultPath = "apps/werewolf/build/outputs/apk/debug/werewolf-debug.apk",
        name = "werewolf-debug-v$werewolfVersionName.apk",
        outputName = "werewolfDebugApkPath",
        envFile = envFile
    )
}

fun Create_pr_assets.createWerewolfReleaseAssets(werewolfVersionName: String, envFile: File ) {
    makeAsset(
        gradleCommand = ":apps:werewolf:assembleRelease",
        defaultPath = "apps/werewolf/build/outputs/apk/release/werewolf-release.apk",
        name = "werewolf-release-v$werewolfVersionName.apk",
        outputName = "werewolfReleaseApkPath",
        envFile = envFile
    )

    makeAsset(
        gradleCommand = ":apps:werewolf:bundleRelease",
        defaultPath = "apps/werewolf/build/outputs/apk/release/werewolf-release.aab",
        name = "werewolf-release-v$werewolfVersionName.aab",
        outputName = "werewolfReleaseAabPath",
        envFile = envFile
    )
}

fun Create_pr_assets.createSpyfallDebugAssets(spyfallVersionName: String, envFile: File ) {
    makeAsset(
        gradleCommand = ":apps:spyfall:assembleLegacyDebug",
        defaultPath = "apps/spyfall/build/outputs/apk/legacy/debug/spyfall-legacy-debug.apk",
        name = "spyfall-legacy-debug-v$spyfallVersionName.apk",
        outputName = "spyfallLegacyDebugApkPath",
        envFile = envFile
    )

    makeAsset(
        gradleCommand = ":apps:spyfall:assembleRefactorDebug",
        defaultPath = "apps/spyfall/build/outputs/apk/refactor/debug/spyfall-refactor-debug.apk",
        name = "spyfall-refactor-debug-v$spyfallVersionName.apk",
        outputName = "spyfallRefactorDebugApkPath",
        envFile = envFile
    )
}
