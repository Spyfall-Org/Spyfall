#!/usr/bin/env kotlin

@file:Repository("https://maven.google.com")
@file:Repository("https://repo1.maven.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("com.google.firebase:firebase-admin:9.1.1")
@file:DependsOn("com.google.code.gson:gson:2.8.6")
@file:DependsOn("com.google.gms:google-services:4.3.14")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream
import com.google.gson.Gson
import java.io.FileReader

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
    printRed(
        """
               This script takes in a json file and uploads it to the debug db as a new pack 
               Usage: ./scripts/upload_json_pack.main.kts [languageCode] [version]
               Place pack details in the file called pack_to_upload.json in the scripts folder
               
               {
                  "name": "Pack Name",
                  "locations": [
                         {
                            "location": "Location Name",
                            "roles": ["role", "role", "role"...]
                          }
                  ]
               }
        """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {
    val languageCode = args[0]
    val version = args[1]
    val debugServiceAccountJsonFile = File("app/src/debug/service-account-key.json")

    if (!debugServiceAccountJsonFile.isFile) {
        printRed(
            """
                The service - account - key.json files was not found.please run . / scripts / get_secret_files.main.kts to get all the secret files.""".trimIndent()
        )
        return
    }

    val debugDb = getDb(debugServiceAccountJsonFile.absolutePath)
    val jsonFilePath = "scripts/location_pack_to_upload.json"
    val json = FileReader(jsonFilePath).readText()
    val pack = Gson().fromJson(json, Pack::class.java)
    val packMap = pack.items.map {
        it.locationName to it.roles
    }.toMap()

    printGreen("Creating new pack: ${pack.items} in debug db")

    debugDb.collection("versioned-packs")
        .document(version)
        .collection(languageCode)
        .document()
        .set(
            mapOf(
                "type" to "location",
                "name" to pack.name,
            )
        )
        .get()

}

data class Location(
    val locationName: String,
    val roles: List<String>
)

data class Pack(
    val name: String,
    val items: List<Location>
)

@Suppress("TooGenericExceptionCaught")
fun getDb(serviceAccountJsonPath: String): Firestore {
    val serviceAccount = FileInputStream(serviceAccountJsonPath)
    val credentials = GoogleCredentials.fromStream(serviceAccount)

    val options = FirebaseOptions.builder().setCredentials(credentials).build()

    val app = try {
        println("Initializing Firebase app")
        FirebaseApp.initializeApp(options, serviceAccountJsonPath)
    } catch (e: IllegalStateException) {
        println("Firebase app already initialized. $e")
        null
    }
    return com.google.firebase.cloud.FirestoreClient.getFirestore(app)
}

doWork()
