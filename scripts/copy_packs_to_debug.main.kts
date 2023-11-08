#!/usr/bin/env kotlin

@file:Repository("https://maven.google.com")
@file:Repository("https://repo1.maven.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("com.google.firebase:firebase-admin:9.1.1")
@file:DependsOn("com.google.gms:google-services:4.3.14")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream

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
               This script copies packs from production db to debug db.                
               Usage: ./copy_packs.main.kts 
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {
    val releaseServiceAccountJsonFile = File("app/src/release/service-account-key.json")
    val debugServiceAccountJsonFile = File("app/src/debug/service-account-key.json")

    if (!debugServiceAccountJsonFile.isFile || !releaseServiceAccountJsonFile.isFile) {
        printRed(
            """
                one of the service-account-key.json files was not found.
                please run ./scripts/get_secret_files.main.kts to get all the secret files.
            """.trimIndent()
        )
        return
    }

    val releaseDb = getDb(releaseServiceAccountJsonFile.absolutePath)
    val debugDb = getDb(debugServiceAccountJsonFile.absolutePath)

    printGreen("Copying packs from production db to debug db")

    val releasePacksCollection = releaseDb.collection("packs")
    val debugPacksCollection = debugDb.collection("packs")

    // Fetch all documents from the release "packs" collection
    val releasePacks = releasePacksCollection.get().get().documents

    releasePacks.forEach { document ->
        // Create a new document in the debug "packs" collection with the same ID
        val newDocument = debugPacksCollection.document(document.id)

        // Write the data from the release database to the debug database
        newDocument.set(document.data).get()
    }

    printGreen("Done copying packs from production db to debug db")
}


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
