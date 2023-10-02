#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")
@file:DependsOn("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// These file ids can be found in the url of the sharable google drive links to these files
data class FileInfo(val id: String, val pathToStore: String)

val spyfallGoogleServicesFileInfo =
    FileInfo(
        id ="1UIP-nsDLazFCo-OIU3OzFjiBNTeyAjRc",
        pathToStore = "app/google-services.json"
    )

val spyfallServiceAccountKeyFileInfo =
    FileInfo(
        id ="13Q-z85mO3-5JVFhXmQ9191-N465-18PH",
        pathToStore = "app/service-account-key.json"
    )



val fileInfoList = listOf(
    spyfallGoogleServicesFileInfo,
    spyfallServiceAccountKeyFileInfo
)

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
if ( isHelpCall ) {
    printRed("""
        This script collects every required google service.json file 
        from google drive using a service account key file. You must either pass in the path to the service_key.json
        file or have it installed in the root directory. 
        
        You can download the service-key.json file from the following link: 
        https://drive.google.com/file/d/1t456fo07BN9NF0a3e1Ds9KNBccV1X1AQ/view?usp=share_link
        
        Usage: ./create_google_json_files.main.kts [optional serviceAccountKeyPath]
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val serviceAccountKeyPath = args.getOrNull(0) ?: run {
    if ( File("service_key.json").isFile) return@run "service_key.json"
    printRed("""
        You must either pass in the path to the service_key.json
        file or have it installed in the root directory. 
        
        You can download the service_key.json file from the following link: 
        https://drive.google.com/file/d/1t456fo07BN9NF0a3e1Ds9KNBccV1X1AQ/view?usp=share_link
    """.trimIndent())
    @Suppress("TooGenericExceptionThrown")
    throw Exception("No service_key.json file found")
}

fun getFiles() {
    val file = File(serviceAccountKeyPath)
    val credentials = ServiceAccountCredentials.fromStream(file.inputStream())
    val scopedCredentials = credentials.createScoped(listOf(DriveScopes.DRIVE))

    val transport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    val drive = Drive.Builder(transport, jsonFactory, HttpCredentialsAdapter(scopedCredentials)).build()
    var successfulFetches = 0
    fileInfoList.forEach {
        val driveFile = drive.files().get(it.id).execute()

        if (driveFile.mimeType == "application/json") {
            val outputStream = ByteArrayOutputStream()
            drive.files().get(it.id).executeMediaAndDownloadTo(outputStream)
            val outputFile = FileOutputStream(it.pathToStore)
            outputStream.writeTo(outputFile)
        }

        successfulFetches += 1
    }

    if(successfulFetches == fileInfoList.size) {
        printGreen("Downloaded all secret files")
    } else {
        printRed("DID NOT Download all secret files")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }
}

getFiles()
