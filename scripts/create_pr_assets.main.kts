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

@Suppress("MagicNumber")
val argCount = 3

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall || args.size < argCount) {
    @Suppress("MaxLineLength")
    printGreen(
        """
               This script creates the apks needed for PRs and writes the paths of those assets to the env file passed 
               in. Release assets are unsigned unless this is being ran locally. In which case they are signed
               with a debug signing config. 
               
               Usage: ./create_pr_assets.main.kts <is-spyfall-release> <is-werewolf-release> <env-file-path>
               
                <is-spyfall-release> - true if this script is being called from a spyfall release pr
                <is-werewolf-release> - true if this script is being called from a werewolf release pr
                <env-file-path> - The env file path output to    
                
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("UnusedPrivateMember", "MagicNumber")
fun main() {
    val isSpyfallRelease = args[0].toBoolean()
    val isWerewolfRelease = args[1].toBoolean()
    val outputEnvFile = File(args[2])

    val isCIBuild = System.getenv("CI") == "true"

    val spyfallVersionName = getAppVersionName("spyfall")
    val werewolfVersionName = getAppVersionName("werewolf")
    val spyfallVersionCode = getAppVersionCode("spyfall")
    val werewolfVersionCode = getAppVersionCode("werewolf")

    printGreen("Assembling the entire project")
    runGradleCommand("assembleDebug")

    renameSpyfallDebugAssets(spyfallVersionName, outputEnvFile, spyfallVersionCode)
    renameWerewolfDebugAssets(werewolfVersionName, outputEnvFile, werewolfVersionCode)

    if (isSpyfallRelease) {
        runGradleCommand(":apps:spyfall:bundleRelease")
        runGradleCommand(":apps:spyfall:assembleRelease")
        renameSpyfallReleaseAssets(spyfallVersionName, outputEnvFile, isCIBuild, spyfallVersionCode)
    }

    if (isWerewolfRelease) {
        runGradleCommand(":apps:werewolf:bundleRelease")
        runGradleCommand(":apps:werewolf:assembleRelease")
        renameWerewolfReleaseAssets(werewolfVersionName, outputEnvFile, isCIBuild, werewolfVersionCode)
    }
}

fun renameSpyfallReleaseAssets(
    spyfallVersionName: String,
    envFile: File,
    isCIBuild: Boolean,
    buildNumber: String
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/release"),
        name = "spyfall-release-v$spyfallVersionName-$signingSuffix-$buildNumber.apk",
        outputName = "spyfallReleaseApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findAabFile("apps/spyfall/build/outputs/bundle/release"),
        name = "spyfall-release-v$spyfallVersionName-$signingSuffix-$buildNumber.aab",
        outputName = "spyfallReleaseAabPath",
        envFile = envFile
    )
}

fun renameWerewolfReleaseAssets(
    werewolfVersionName: String,
    envFile: File,
    isCIBuild: Boolean,
    buildNumber: String
) {
    val signingSuffix = if (isCIBuild) "unsigned" else "debugSigned"

    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/release"),
        name = "werewolf-release-v$werewolfVersionName-$signingSuffix-$buildNumber.apk",
        outputName = "werewolfReleaseApkPath",
        envFile = envFile
    )

    renameAsset(
        defaultPath = findAabFile("apps/werewolf/build/outputs/bundle/release"),
        name = "werewolf-release-v$werewolfVersionName-$signingSuffix-$buildNumber.aab",
        outputName = "werewolfReleaseAabPath",
        envFile = envFile
    )

}

fun renameWerewolfDebugAssets(werewolfVersionName: String, envFile: File, buildNumber: String) {
    renameAsset(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/debug"),
        name = "werewolf-debug-v$werewolfVersionName-$buildNumber.apk",
        outputName = "werewolfDebugApkPath",
        envFile = envFile
    )
}

fun renameSpyfallDebugAssets(spyfallVersionName: String, envFile: File, buildNumber: String) {

    renameAsset(
        defaultPath = findApkFile("apps/spyfall/build/outputs/apk/debug"),
        name = "spyfall-debug-v$spyfallVersionName-$buildNumber.apk",
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

fun getAppVersionCode(app: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$app.versionCode").toString()
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
