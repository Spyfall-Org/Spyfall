#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")
@file:DependsOn("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.firebase.appdistribution.gradle.models.ServiceAccountCredentials
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

// These file ids can be found in the url of the sharable google drive links to these files
data class FileInfo(val id: String, val pathToStore: String)
val spyfallGoogleServicesFileInfo = FileInfo(id ="1uieO42nwfDV6E1_EIFoBgDMRy0A5cBcy", pathToStore = "apps/spyfall/google-services.json")
val spyfallServiceAccountKeyFileInfo = FileInfo(id ="1uSnJx6Xr4nx4alpNHsAtv57gvgeb34cZ", pathToStore = "apps/spyfall/service-account-key.json")

val werewolfGoogleServicesFileInfo = FileInfo(id ="1DCmIFGyqAzwd79CvOFi72Gf7rcksmW8B", pathToStore = "apps/werewolf/google-services.json")
val werewolfServiceAccountKeyFileInfo = FileInfo(id ="1dP6c2fjc5BPecvyOKRk08ZZv8yxrIRnC", pathToStore = "apps/werewolf/service-account-key.json")

val fileInfoList = listOf(
    spyfallGoogleServicesFileInfo,
    spyfallServiceAccountKeyFileInfo,
    werewolfGoogleServicesFileInfo,
    werewolfServiceAccountKeyFileInfo
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
if ( isHelpCall || args.isEmpty()) {
    printRed("""
        This script collects every required google-services.json file 
        from google drive using a service account key passed in
        
        Usage: ./create_google_json_files.main.kts [serviceAccountKey]
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val serviceAccountKeyString = args[0]

fun getFiles() {
    val inputStream = ByteArrayInputStream(serviceAccountKeyString.toByteArray())
    val credentials = ServiceAccountCredentials.fromStream(inputStream)

    val transport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    val drive = Drive.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials)).build()

    fileInfoList.forEach {
        val driveFile = drive.files().get(it.id).execute()

        if (driveFile.mimeType == "application/json") {
            val outputStream = ByteArrayOutputStream()
            drive.files().get(it.id).executeMediaAndDownloadTo(outputStream)
            val outputFile = FileOutputStream(it.pathToStore)
            outputStream.writeTo(outputFile)
        }
    }
}

getFiles()
