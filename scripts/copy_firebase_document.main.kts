#!/usr/bin/env kotlin

@file:Repository("https://maven.google.com")
@file:Repository("https://repo1.maven.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("com.google.firebase:firebase-admin:9.1.1")
@file:DependsOn("com.google.gms:google-services:4.3.14")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")
@file:Suppress("TooGenericExceptionThrown", "ThrowsCount")

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream

val red = "\u001b[31m"
val green = "\u001b[32m"
val yellow = "\u001b[33m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isCIBuild = System.getenv("CI") == "true"

fun printYellow(text: String) {
    println(yellow + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    @Suppress("MaxLineLength")
    printYellow(
        """
               This script copies a document from from any env to another document in any env            
               Usage: ./copy_firebase_document.main.kts [documentpath] [env] [documentpath] [env]
               [documentpath] - The path to the document in the firestore database. Example: /config-android/1.2.5
               [env] - The environment to copy the document to. Example: debug or release
               Examples: 
               ./scripts/copy_firebase_document.main.kts config-android/1.2.5 debug config-android/1.2.5 release
               ./scripts/copy_firebase_document.main.kts dictionary-overrides-android/1.2.6 debug dictionary-overrides-android/1.2.6 release
  
    """.trimIndent()
    )

    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {
    val documentPathOne = args[0]
    val envOne = args[1]
    val documentPathTwo = args[2]
    val envTwo = args[3]

    val releaseServiceAccountJsonFile = File("app/src/release/service-account-key.json")
    val debugServiceAccountJsonFile = File("app/src/debug/service-account-key.json")

    if (!debugServiceAccountJsonFile.isFile || !releaseServiceAccountJsonFile.isFile) {
        printRed(
            """
                one of the service-account-key.json files was not found.
                please run ./scripts/get_secret_files.main.kts to get all the secret files.
            """.trimIndent()
        )
        throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")

    }

    if (envOne !in listOf("release", "debug") || envTwo !in listOf("release", "debug") ) {
        printRed("Invalid environments [$envOne, ${envTwo}]. Please use either release or debug")
        throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
    }

    val releaseDb = getDb(releaseServiceAccountJsonFile.absolutePath)
    val debugDb = getDb(debugServiceAccountJsonFile.absolutePath)

    printGreen("You will be copying $documentPathOne from $envOne db to $documentPathTwo document in $envTwo db.")

    if (!isCIBuild) {
        printYellow("Press enter to continue")
        readLine()
    }

    val documentOne = if (envOne == "release") {
        releaseDb.document(documentPathOne).get().get()
    } else {
        debugDb.document(documentPathOne).get().get()
    }

    val uploadPath = if (envTwo == "release") {
        releaseDb.document(documentPathTwo)
    } else {
        debugDb.document(documentPathTwo)
    }

    val documentOneData = documentOne.data

    if (documentOneData == null) {
        printRed("Document $documentPathOne does not exist in $envOne db")
        throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
    } else {
        printGreen("Initiating copy...")
        uploadPath.set(documentOneData).get()
        printGreen("Done copying document $documentPathOne from $envOne to $envTwo db.")
    }
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
