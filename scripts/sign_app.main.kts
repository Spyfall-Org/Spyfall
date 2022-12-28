#!/usr/bin/env kotlin

import java.io.File
import java.io.FileWriter

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
@Suppress("MaxLineLength")
if ( isHelpCall ) {
    printRed("""
        This script signs the app based on the apk or aab with a given path. This is mainly used by our CI. 
        
        Usage: ./sign_app.main.kts [assetPath] [keystorePath] [keyStorePassword] [keystoreAlias] [signingKey] [outputName] [envFile]
        [assetPath] - path to apk or aab to sign
        [keystorePath] - path to they keystore
        [keyStorePassword] - password for keystore
        [keystoreAlias] - alias for keystore
        [signingKey] - signing key
        [outputName] - the key in the key value pairing for output to the ENV file
        [envFile] - the env file to output the final path to the signed apk. 
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("ThrowsCount", "MagicNumber")
fun main() {
    val assetPath = args[0]
    val keystorePath = args[1]
    val keystorePassword = args[3]
    val keystoreAlias = args[2]
    val signingKey = args[4]
    val outputName = args[5]
    val envFile = File(args[6])

    val assetFile = File(assetPath).also { if (!it.isFile) throw  FileDoesNoteExistError(it.absolutePath) }
    val keystoreFile = File(keystorePath).also { if (!it.isFile) throw  FileDoesNoteExistError(it.absolutePath) }

    @Suppress("MaxLineLength")
    val signingCommand = when (assetFile.extension) {
        "apk" -> {
            "jarsigner -keystore $keystoreFile $assetFile $keystoreAlias -storepass $keystorePassword -keypass $signingKey"
        }
        "aab" -> {
            "java -jar bundletool.jar build-apks --bundle $assetFile --output signed.apks --ks $keystoreFile --ks-key-alias $keystoreAlias --ks-pass pass:$keystorePassword --key-pass pass:$signingKey"
        }
        else -> throw FileExtensionError(assetFile.extension)
    }

    val signingProcess = Runtime.getRuntime().exec(signingCommand)
    signingProcess.waitFor()
    renameAndWriteToOutput(assetPath, outputName, envFile)
}

class FileDoesNoteExistError(path: String) : Exception("The file $path does not exist.")

class FileExtensionError(ext: String): Exception("File ext $ext does not match aab or apk. ")

fun renameAndWriteToOutput(defaultPath: String, outputName: String, envFile: File ) {
    val apkFile = File(defaultPath)
    val newName = apkFile.name.removePrefix("devSigned-").replace("unsigned", "signed")
    val newFile = File(apkFile.parent, newName)
    val didRename = apkFile.renameTo(newFile)
    val finalPath = if(didRename) newFile.absolutePath else apkFile.absolutePath

    val writer = FileWriter(envFile, true)
    writer.write("$outputName=$finalPath")
    writer.write("\n")
    writer.close()
}

main()
