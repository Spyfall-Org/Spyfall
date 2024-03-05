#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.OutputStreamWriter
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
        
        usage: ./deploy_app_distribution.main.kts <app_name> <app_id> <assets>
        app_id: The firebase app id for the app
        firebaseToken: the token used to contact firebase. Stored as a secret on github
        envFile: File where constants are held between ci jobs
        pullrequestLink: The string used to link the firebase release to teh initiating PR
        isRelease: Denotes if the PR that triggered this is a release PR
        linkKey: env file key to store the link returned from the firebase upload
        assets: a comma separated list of asset keys used in the env file
    """.trimIndent()
    )
}

@Suppress("MagicNumber")
fun main() {
    val appId = args[0]
    val firebaseToken = args[1]
    val envFile = File(args[2])
    val pullRequestLink = args[3]
    val isRelease = args[4].toBoolean()
    val linkKey = args[5]
    val assetPaths = args.slice(6.until(args.size))
    val versionCode = getAppVersionCode()
    val versionName = getAppVersionName()

    val testerFilePath = "app/src/release/testers.txt"
    val serviceAccountPath = "app/src/release/service-account-key.json"

    installNode()
    installFirebase()

    System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", serviceAccountPath)
    System.setProperty("FIREBASE_TOKEN", firebaseToken)

    val writer = FileWriter(envFile, true)
    writer.write("GOOGLE_APPLICATION_CREDENTIALS=$serviceAccountPath")
    writer.appendLine()
    writer.write("FIREBASE_TOKEN=$firebaseToken")
    writer.appendLine()
    writer.close()

    assetPaths.forEach { path ->
        println("Uploading asset ${File(path).name} to firebase distribution")
        uploadToFirebaseAppDistribution(
            appId,
            path,
            testerFilePath,
            pullRequestLink,
            isRelease,
            versionName,
            versionCode,
            envFile,
            linkKey
        )
        println("Finished Uploading asset ${File(path).name} to firebase distribution")
    }
}

@Suppress("LongParameterList")
fun uploadToFirebaseAppDistribution(
    appId: String,
    apkPath: String,
    testerFilePath: String,
    pullRequestLink: String,
    isRelease: Boolean,
    versionName: String,
    versionCode: String,
    file: File,
    linkKey: String
) {

    val releaseNotes = """
        ${if (isRelease) "RELEASE" else "DEBUG"}
        
        This asset was generated based off the following pull request: 
        $pullRequestLink
        
        App Version: $versionName
        Version Code (Build Number): $versionCode        
    """.trimIndent()

    val releaseNotesPath = File("firebase_release_notes.txt").apply {
        createNewFile()
        val releaseNotesWriter = writer()
        releaseNotesWriter.write(releaseNotes)
        releaseNotesWriter.close()
    }.path

    @Suppress("MaxLineLength")
    val uploadCommand = "firebase appdistribution:distribute --app $appId --release-notes-file $releaseNotesPath --testers-file $testerFilePath $apkPath"
    printGreen("Running Command\n\n$uploadCommand")
    runCatching { runCommandLine(uploadCommand) }
        .onSuccess {
            printGreen("Successfully uploaded apk to firebase app distribution")

            val consoleLink = "(https://console.firebase.google.com/[\\w/\\-.?=&]+)"
                .toRegex()
                .find(it)
                ?.groupValues
                ?.firstOrNull()

            val writer = FileWriter(file, true)

            if (consoleLink!= null) {
                printGreen("adding link for $linkKey\n\n$consoleLink")
                writer.writeEnvValue(linkKey, consoleLink)
            } else {
                printRed("No link found from upload response. $it")
                writer.writeEnvValue(linkKey, "null")
            }
        }
        .onFailure {
            printRed("Failed to upload to firebase app distribution")
            throw it
        }
}

fun File.deleteKey(key: String) {
    val lines = readLines().filter { !it.contains("$key=") }
    writeText(lines.joinToString("\n"))
}

fun OutputStreamWriter.writeEnvValue(key: String, value: String): OutputStreamWriter {
    write("$key=$value")
    write("\n")
    return this
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

@Suppress("SpreadOperator")
fun runCommandLine(command: String): String {
    val process = ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    val error = process.errorStream.bufferedReader().readText()

    if (error.isNotEmpty()) {
        printRed("ERROR")
        printYellow("\n\n$error\n\n")
        if (error.contains("Error:") || error.contains("error:")) {
            throw IllegalStateException(error)
        }
    }

    if (output.isNotEmpty()) {
        printYellow("OUTPUT")
        printYellow("\n\n$output\n\n")
        if (output.contains("Error:") || output.contains("error:")) {
            throw IllegalStateException(error)
        }
    }

    process.waitFor()

    return output
}

fun getAppVersionName(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("versionName").toString()
}

fun getAppVersionCode(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("versionCode").toString()
}

main()
