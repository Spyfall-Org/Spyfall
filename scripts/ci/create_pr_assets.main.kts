#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Base64
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
               
               Usage: ./create_pr_assets.main.kts <is-spyfall-release> <is-werewolf-release> <env-file-path> <signingKeyBase64> <keystorePassword> <keystoreAlias> <signingKey>
               
                <is-spyfall-release> - true if this script is being called from a spyfall release pr
                <is-werewolf-release> - true if this script is being called from a werewolf release pr
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
    val isSpyfallRelease = args[0].toBoolean()
    val isWerewolfRelease = args[1].toBoolean()
    val outputEnvFile = File(args[2])
    val keystoreBase64 = args[3]
    val keystorePassword = args[4]
    val keystoreAlias = args[5]
    val keyPassword = args[6]

    printGreen("Decoding keystore content from base 64")
    val decodedKeystore = Base64.getDecoder().decode(keystoreBase64).toString()
    val keystore = File("spyfall.keystore")
    keystore.createNewFile()

    keystore.writer().let {
        it.write(decodedKeystore)
        it.appendLine()
        it.close()
    }

    val isCIBuild = System.getenv("CI") == "true"

    val spyfallVersionName = getAppVersionName("spyfall")
    val werewolfVersionName = getAppVersionName("werewolf")
    val spyfallVersionCode = getAppVersionCode("spyfall")
    val werewolfVersionCode = getAppVersionCode("werewolf")

    printGreen("Assembling all debug assets")
    runGradleCommand("assembleDebug")

    renameSpyfallDebugAssets(spyfallVersionName, outputEnvFile, spyfallVersionCode)
    renameWerewolfDebugAssets(werewolfVersionName, outputEnvFile, werewolfVersionCode)

    if (isSpyfallRelease) {
        printGreen("Assembling all spyfall release assets")
        runGradleCommand(":apps:spyfall:bundleRelease")
        runGradleCommand(":apps:spyfall:assembleRelease")
        signAndRenameSpyfallReleaseAssets(
            spyfallVersionName,
            outputEnvFile,
            isCIBuild,
            spyfallVersionCode,
            keystore,
            keystoreAlias,
            keystorePassword,
            keyPassword
        )
    }

    if (isWerewolfRelease) {
        printGreen("Assembling all werewolf release assets")
        runGradleCommand(":apps:werewolf:bundleRelease")
        runGradleCommand(":apps:werewolf:assembleRelease")
        signAndRenameWerewolfReleaseAssets(
            werewolfVersionName,
            outputEnvFile,
            isCIBuild,
            werewolfVersionCode,
            keystore,
            keystoreAlias,
            keystorePassword,
            keyPassword
        )
    }
}

@Suppress("LongParameterList")
fun signAndRenameSpyfallReleaseAssets(
    spyfallVersionName: String,
    envFile: File,
    isCIBuild: Boolean,
    buildNumber: String,
    keystoreFile: File,
    storeAlias: String,
    keystorePassword: String,
    keyPassword: String
) {
    val signingSuffix = if (isCIBuild) "signed" else "debugSigned"

    val apkAsset = File(findApkFile("apps/spyfall/build/outputs/apk/release"))
    val aabAsset = File(findAabFile("apps/spyfall/build/outputs/bundle/release"))

    runCommandLine(
        "./scripts/sign_app.main.kts",
        apkAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "spyfall-release-v$spyfallVersionName-$signingSuffix-$buildNumber.apk",
        "spyfallReleaseApkPath",
        envFile.path
    )

    runCommandLine(
        "./scripts/sign_app.main.kts",
        aabAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "spyfall-release-v$spyfallVersionName-$signingSuffix-$buildNumber.aab",
        "spyfallReleaseAabPath",
        envFile.path
    )
}

@Suppress("LongParameterList")
fun signAndRenameWerewolfReleaseAssets(
    werewolfVersionName: String,
    envFile: File,
    isCIBuild: Boolean,
    buildNumber: String,
    keystoreFile: File,
    storeAlias: String,
    keystorePassword: String,
    keyPassword: String
) {
    val apkAsset = File(findApkFile("apps/werewolf/build/outputs/apk/release"))
    val aabAsset = File(findAabFile("apps/werewolf/build/outputs/bundle/release"))

    val signingSuffix = if (isCIBuild) "signed" else "debugSigned"

    runCommandLine(
        "./scripts/sign_app.main.kts",
        apkAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "werewolf-release-v$werewolfVersionName-$signingSuffix-$buildNumber.apk",
        "werewolfReleaseApkPath",
        envFile.path
    )

    runCommandLine(
        "./scripts/sign_app.main.kts",
        aabAsset.path,
        keystoreFile.path,
        keystorePassword,
        storeAlias,
        keyPassword,
        "werewolf-release-v$werewolfVersionName-$signingSuffix-$buildNumber.aab",
        "werewolfReleaseAabPath",
        envFile.path
    )
}

fun renameWerewolfDebugAssets(werewolfVersionName: String, envFile: File, buildNumber: String) {
    setOutputAssetName(
        defaultPath = findApkFile("apps/werewolf/build/outputs/apk/debug"),
        name = "werewolf-debug-v$werewolfVersionName-$buildNumber.apk",
        outputName = "werewolfDebugApkPath",
        envFile = envFile
    )
}

fun renameSpyfallDebugAssets(spyfallVersionName: String, envFile: File, buildNumber: String) {
    setOutputAssetName(
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
            throw IllegalStateException(error)
        }
    }

    process.waitFor()

    return output
}

class FileDoesNoteExistError(path: String) : Exception("The file $path does not exist.")

class FileExtensionError(ext: String) : Exception("File ext $ext does not match aab or apk. ")

main()
