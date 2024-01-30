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
               in. Release assets are signed unless this is being ran locally. In which case they are signed
               with a debug signing config. 
               
               Usage: ./create_pr_assets.main.kts <is-release> <env-file-path> <signingKeyBase64> <keystorePassword> <keystoreAlias> <signingKey>
               
                <is-release> - true if this script is being called from a oddoneout release pr
                <env-file-path> - The env file path output to  
                <signingKeyBase64> - the keystore in base 64 format
                <keyStorePassword> - password for keystore
                <keystoreAlias> - alias for keystore
                <signingKey> - signing key
               
                
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("UnusedPrivateMember", "MagicNumber")
fun main() {
    val isRelease = args[0].toBoolean()
    val outputEnvFile = File(args[1])
    val keystorePath = args[2]
    val keystorePassword = args[3]
    val keystoreAlias = args[4]
    val keyPassword = args[5]

    val keystore = File(keystorePath)

    val isCIBuild = System.getenv("CI") == "true"

    val versionName = getAppVersionName()
    val versionCode = getAppVersionCode()


    if (isRelease) {

        printGreen("Assembling all debug assets")
        runGradleCommand(":app:assembleDebug --debug")

        renameDebugAssets(versionName, outputEnvFile, versionCode)

        printGreen("Assembling all release assets")
        runGradleCommand(":app:bundleRelease")
        runGradleCommand(":app:assembleRelease")

        signAndRenameReleaseAssets(
            versionName,
            outputEnvFile,
            isCIBuild,
            versionCode,
            keystore,
            keystoreAlias,
            keystorePassword,
            keyPassword
        )
    }
}

@Suppress("LongParameterList")
fun signAndRenameReleaseAssets(
    versionName: String,
    envFile: File,
    isCIBuild: Boolean,
    buildNumber: String,
    keystoreFile: File,
    storeAlias: String,
    keystorePassword: String,
    keyPassword: String
) {
    val signingSuffix = if (isCIBuild) "signed" else "debugSigned"

    val apkAsset = File(findApkFile("app/build/outputs/apk/release"))
    val aabAsset = File(findAabFile("app/build/outputs/bundle/release"))

    runCommandLine(
        "./scripts/sign_app.main.kts",
        apkAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "oddoneout-release-v$versionName-$signingSuffix-$buildNumber.apk",
        "oddoneoutReleaseApkPath",
        envFile.path
    )

    runCommandLine(
        "./scripts/sign_app.main.kts",
        aabAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "oddoneout-release-v$versionName-$signingSuffix-$buildNumber.aab",
        "oddoneoutReleaseAabPath",
        envFile.path
    )
}

fun renameDebugAssets(versionName: String, envFile: File, buildNumber: String) {
    setOutputAssetName(
        defaultPath = findApkFile("app/build/outputs/apk/debug"),
        name = "oddoneout-debug-v$versionName-$buildNumber.apk",
        outputName = "oddoneoutlDebugApkPath",
        envFile = envFile
    )
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


fun setOutputAssetName(defaultPath: String, name: String, outputName: String, envFile: File) {
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

fun runGradleCommand(command: String) = runCommandLine("./gradlew",command)

@Suppress("SpreadOperator")
fun runCommandLine(command: String) = runCommandLine(command.split("\\s".toRegex()).toTypedArray().toList())

fun runCommandLine(vararg commands: String) = runCommandLine(commands.toList())

fun runCommandLine(command: List<String>): String {
    val process = ProcessBuilder(command)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    val error = process.errorStream.bufferedReader().readText()

    if (error.isNotEmpty()) {
        printRed("\n\n$error\n\n")
        if (error.contains("Error:") || error.contains("error:")) {
            throw IllegalStateException(error)
        }
    }

    if (output.isNotEmpty()) {
        println("\n\n$output\n\n")
        if (output.contains("Error:") || output.contains("error:")) {
            printRed(output)
            throw IllegalStateException(error)
        }
    }

    process.waitFor()

    return output
}

class FileDoesNoteExistError(path: String) : Exception("The file $path does not exist.")

class FileExtensionError(ext: String) : Exception("File ext $ext does not match aab or apk. ")

main()
