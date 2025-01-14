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
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

// These file ids can be found in the url of the sharable google drive links to these files
// ex: https://drive.google.com/file/d/1IIkODW-U5wvumwAk7SrHWxSil54Wp92Y/view?usp=drive_link
// id would be 1IIkODW-U5wvumwAk7SrHWxSil54Wp92Y
data class FileInfo(val id: String, val pathsToStore: List<String>)

// Used to talk to the release firebase project
val releaseGoogleServicesFileInfo =
    FileInfo(
        id = "1A9PKNRMCpLFOg-2MG9RZKaCVbI7J_Jy-",
        pathsToStore = listOf(
            "app/src/release/google-services.json",
            "app/src/qa/google-services.json",
        )
    )

// Used to make changes to the release firestore project via scripts
val releaseServiceAccountKeyFileInfo =
    FileInfo(
        id = "193oc95Afiawi840iJxLkurNNzAg-yR01",
        pathsToStore = listOf(
            "app/src/release/service-account-key.json",
            "app/src/qa/service-account-key.json",
        )
    )

// Used to talk to the debug firebase project
val debugGoogleServicesFileInfo =
    FileInfo(
        id = "1IIkODW-U5wvumwAk7SrHWxSil54Wp92Y",
        pathsToStore = listOf("app/src/debug/google-services.json")
    )

// Used to make changes to the debug firestore project via scripts
val debugServiceAccountKeyFileInfo =
    FileInfo(
        id = "12Di5JKR_hEjaA5JAvGVqp2aEUO8PNbO1",
        pathsToStore = listOf("app/src/debug/service-account-key.json")
    )

val fileInfoList = listOf(
    releaseGoogleServicesFileInfo,
    releaseServiceAccountKeyFileInfo,
    debugGoogleServicesFileInfo,
    debugServiceAccountKeyFileInfo,
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
if (isHelpCall) {
    printRed(
        """
        This script collects every required files to run the debug and release variants
        from google drive using a service account key file. You must either pass in the path to the service_key.json
        file or have it installed in the root directory. 
        
        You can download the service-key.json file from the following link: 
        https://drive.google.com/file/d/1t456fo07BN9NF0a3e1Ds9KNBccV1X1AQ/view?usp=share_link
        
        Usage: ./create_google_json_files.main.kts [optional serviceAccountKeyPath]
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

val serviceAccountKeyPath = args.getOrNull(0) ?: run {
    if (File("service_key.json").isFile) return@run "service_key.json"
    printRed(
        """
        You must either pass in the path to the service_key.json
        file or have it installed in the root directory. 
        
        You can download the service_key.json file from the following link: 
        https://drive.google.com/file/d/1t456fo07BN9NF0a3e1Ds9KNBccV1X1AQ/view?usp=share_link
    """.trimIndent()
    )
    @Suppress("TooGenericExceptionThrown")
    throw Exception("No service_key.json file found")
}

@Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
fun getFiles() {
    val googleDriveServiceAccountKey = File(serviceAccountKeyPath)
    val credentials = ServiceAccountCredentials.fromStream(googleDriveServiceAccountKey.inputStream())
    val scopedCredentials = credentials.createScoped(listOf(DriveScopes.DRIVE))

    val transport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    val drive = Drive.Builder(
        transport,
        jsonFactory,
        HttpCredentialsAdapter(scopedCredentials)
    )
        .setApplicationName("Odd One Out")
        .build()

    println("Current working directory: ${System.getProperty("user.dir")}")

    ls()

    var successfulFetches = 0
    fileInfoList.forEach {
        try {

            it.pathsToStore.forEach { path ->
                val file = File(path)
                if (!file.isFile) {
                    file.createNewFile()
                }
            }

            val driveFile = drive.files().get(it.id).execute()

            if (driveFile.mimeType == "application/json") {
                val outputStream = ByteArrayOutputStream()
                drive.files().get(it.id).executeMediaAndDownloadTo(outputStream)
                val outputFiles = it.pathsToStore.map { path ->
                    FileOutputStream(path)
                }

                outputFiles.forEach { output ->
                    outputStream.writeTo(output)
                }
            }

            successfulFetches += 1
        } catch (t: Throwable) {
            printRed("Error getting file ${it.pathsToStore}")
            t.printStackTrace()
        }
    }

    if (successfulFetches == fileInfoList.size) {
        printGreen("Downloaded all secret files")
    } else {
        printRed("DID NOT Download all secret files")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }
}

@Suppress("PrintStackTrace")
fun ls() {
    try {
        val process = Runtime.getRuntime().exec("ls -la")
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

getFiles()
