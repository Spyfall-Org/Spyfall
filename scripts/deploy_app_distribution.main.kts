#!/usr/bin/env kotlin

@file:Import(
    "data/AndroidClientInfo.kt",
    "data/ApiKey.kt",
    "data/AppinviteService.kt",
    "data/Client.kt",
    "data/ClientInfo.kt",
    "data/GoogleServices.kt",
    "data/IosInfo.kt",
    "data/OauthClient.kt",
    "data/OtherPlatformOauthClient.kt",
    "data/ProjectInfo.kt",
    "data/Services.kt",
)
@file:Import("util/GithubActionsUtil.main.kts")
@file:DependsOn("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Properties

val red = "\u001b[31m"
val green = "\u001b[32m"
val yellow = "\u001b[33m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printYellow(text: String) {
    println(yellow + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
@Suppress("MagicNumber")
if (isHelpCall || args.size < 3) {
    printRed(
        """
        This script uploads assets to firebase distribution
        
        usage: ./deploy_app_distribution.main.kts <app_name> <env_file_path> <assets>
        app_name: The name of the app we are uploading assets for [spyfall, werewolf...]
        env_file_path: The path to the env file containing the asset paths
        assets: a comma separated list of asset keys used in the env file
    """.trimIndent()
    )
}
fun main() {

    val appName = args[0]
    val envFile = File(args[1])
    val assetKeys = args[2].split(",").map { it.trim() }
    val serviceAccountPath = "apps/$appName/service-account-key.json"

    installNode()
    installFirebase()

    val appId = envFile.getValue("${appName}AppId")

    check(appId != null) { "Could not find app id for app ${appName}" }

    System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", serviceAccountPath)

    assetKeys.mapNotNull { key -> envFile.getEnvValue(key) }.forEach { path ->
        println("Uploading asset ${File(path).name} to firebase distribution")
        uploadToFirebaseAppDistribution(appId, path)
        println("Finished Uploading asset ${File(path).name} to firebase distribution")
    }
}



fun uploadToFirebaseAppDistribution(appId: String, apkPath: String) {
    @Suppress("MaxLineLength")
    val uploadCommand = "firebase appdistribution:distribute --app $appId --groups default $apkPath"
    printGreen("Running Command\n\n$uploadCommand")
    runCatching { runCommandLine(uploadCommand) }
        .onSuccess { printGreen("Successfully uploaded apk to firebase app distribution") }
        .onFailure {
            printRed("Failed to upload to firebase app distribution")
            throw it
        }
}

fun installFirebase() {
    println("Checking if firebase tools is installed")
    val isFirebaseInstalled = runCatching { runCommandLine("firebase --version") }.isSuccess
    if (!isFirebaseInstalled) {
        println("Firebase tools was not found.")
        println("Installing firebase tools")
        runCatching { runCommandLine("npm install -g firebase-tools") }
            .onSuccess { printGreen("Successfully installed firebase tools") }
            .onFailure {
                printRed("Error installing firebase tools")
                throw it
            }
    }
}

fun installNode() {
    println("Checking if node is installed")
    val isNodeInstalled = runCatching { runCommandLine("node --version") }.isSuccess

    if (!isNodeInstalled) {
        println("Node was not found. Installing now...")
        val installNodeCommand = getNodeInstallCommand()
        runCatching { runCommandLine(installNodeCommand) }
            .onSuccess { printGreen("Node was successfully installed") }
            .onFailure { printRed("Could not install node. $it") }
    }
}

fun getNodeInstallCommand() = when {
    runCatching { runCommandLine("apt-get") }.isSuccess -> "apt-get update && apt-get install -y nodejs"
    runCatching { runCommandLine("yum") }.isSuccess -> "yum install nodejs"
    runCatching { runCommandLine("brew") }.isSuccess -> "brew install node"
    else -> throw IllegalStateException(
        """
        No package manager could be found on this system to install node
    """.trimIndent()
    )
}

fun runCommandLine(command: String): String {
    val process = ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)

        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    val error = process.errorStream.bufferedReader().readText()

    if (error.isNotEmpty()) {
        printYellow("\n\n$error\n\n")
        if (error.contains("Error:") || error.contains("error:")) {
            throw IllegalStateException(error)
        }
    }

    if (output.isNotEmpty()) {
        printYellow("\n\n$output\n\n")
        if (output.contains("Error:") || output.contains("error:")) {
            throw IllegalStateException(error)
        }
    }

    process.waitFor()

    return output
}

main()