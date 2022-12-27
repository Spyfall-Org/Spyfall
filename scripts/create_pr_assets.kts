#!/usr/bin/env kotlin

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
               
               Usage: ./create_pr_assets.kts [ENV_FILE] [IS_SPYFALL_RELEASE] [IS_WEREWOLF_RELEASE]
               ENV_FILE: The env file to output to
               IS_SPYFALL_RELEASE: If the PR that triggered this is a spyfall release
               IS_WEREWOLF_RELEASE: If the PR that triggered this is a werewolf release               
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

fun main() {
    val envFile = File(args.get(0))
    val isSpyfallRelease = args.getOrNull(1)?.toBoolean() ?: false
    val isWerewolfRelease = args.getOrNull(2)?.toBoolean() ?: false

    val isCIBuild = System.getenv("CI") == "true"

    val spyfallVersionName = getAppVersionName("spyfall")
    val werewolfVersionName = getAppVersionName("werewolf")

    printGreen("Assembling the entire project")
    runGradleCommand("assemble")

    if (isSpyfallRelease) {
        runGradleCommand(":apps:spyfall:bundleLegacyRelease")
        runGradleCommand(":apps:spyfall:bundleRefactorRelease")
    }

    if (isWerewolfRelease) {
        runGradleCommand(":apps:werewolf:bundleRelease")
    }

    renameSpyfallDebugAssets(spyfallVersionName, envFile)
    renameSpyfallReleaseAssets(spyfallVersionName, envFile, isSpyfallRelease, isCIBuild)

    renameWerewolfDebugAssets(werewolfVersionName, envFile)
    renameWerewolfReleaseAssets(werewolfVersionName, envFile, isWerewolfRelease, isCIBuild)
}

fun renameSpyfallReleaseAssets(
    spyfallVersionName: String,
    envFile: File,
    isSpyfallRelease: Boolean,
    isCIBuild: Boolean
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/legacy/release"),
        name = "spyfall-legacy-release-v$spyfallVersionName-$signingSuffix.apk",
        outputName = "spyfallLegacyReleaseApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/refactor/release"),
        name = "spyfall-refactor-release-v$spyfallVersionName-$signingSuffix.apk",
        outputName = "spyfallRefactorReleaseApkPath",
        envFile = envFile
    )

    if (isSpyfallRelease) {
        renameAsset(
            defaultPath = findAabFile("apps/spyfall/build/outputs/bundle/refactorRelease"),
            name = "spyfall-refactor-release-v$spyfallVersionName-$signingSuffix.aab",
            outputName = "spyfallRefactorReleaseAabPath",
            envFile = envFile
        )

        renameAsset(
            defaultPath = findAabFile("apps/spyfall/build/outputs/bundle/legacyRelease"),
            name = "spyfall-legacy-release-v$spyfallVersionName-$signingSuffix.aab",
            outputName = "spyfallLegacyReleaseAabPath",
            envFile = envFile
        )
    }
}

fun renameWerewolfDebugAssets(werewolfVersionName: String, envFile: File ) {
    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/debug"),
        name = "werewolf-debug-v$werewolfVersionName.apk",
        outputName = "werewolfDebugApkPath",
        envFile = envFile
    )
}

fun renameWerewolfReleaseAssets(
    werewolfVersionName: String,
    envFile: File,
    isWerewolfRelease: Boolean,
    isCIBuild: Boolean
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/release"),
        name = "werewolf-release-v$werewolfVersionName-$signingSuffix.apk",
        outputName = "werewolfReleaseApkPath",
        envFile = envFile
    )

    if (isWerewolfRelease) {
        renameAsset(
            defaultPath = findAabFile("apps/werewolf/build/outputs/bundle/release"),
            name = "werewolf-release-v$werewolfVersionName-$signingSuffix.aab",
            outputName = "werewolfReleaseAabPath",
            envFile = envFile
        )
    }
}

fun renameSpyfallDebugAssets(spyfallVersionName: String, envFile: File ) {
    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/legacy/debug"),
        name = "spyfall-legacy-debug-v$spyfallVersionName.apk",
        outputName = "spyfallLegacyDebugApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/refactor/debug"),
        name = "spyfall-refactor-debug-v$spyfallVersionName.apk",
        outputName = "spyfallRefactorDebugApkPath",
        envFile = envFile
    )
}

fun getAppVersionName(app: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app_versions.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$app.versionName").toString()
}


fun renameAsset(defaultPath: String, name: String, outputName: String, envFile: File ) {
    val apkFile = File(defaultPath)
    apkFile.renameTo(File(apkFile.parent, name))
    val finalPath = apkFile.absolutePath

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
