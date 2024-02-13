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

val isCIBuild = System.getenv("CI") == "true"

fun printGreen(text: String) {
    println(green + text + reset)
}

fun printYellow(text: String) {
    println(yellow + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    @Suppress("MaxLineLength")
    printYellow(
        """
               This script copies a document from from any env to another document in any env            
               Usage: ./copy_firebase_document.main.kts [collectionTOCopy] [env] [collectionToCopyTo] [env]
               [collectionTOCopy] - The path to the document in the firestore database. Example: packs
               [env] - The environment to copy the document to. Example: debug or release
               Examples: 
               ./scripts/copy_firebase_collection.main.kts packs debug packs release
  
    """.trimIndent()
    )

    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {
    val collectionToCopy = args[0]
    val envOne = args[1]
    val collectionToCopyTo = args[2]
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

    printGreen("You will be copying $collectionToCopy from $envOne db to $collectionToCopyTo in $envTwo db.")

    if (!isCIBuild) {
        printYellow("Press enter to continue")
        readLine()
    }

    val collectionOne = if (envOne == "release") {
        releaseDb.collection(collectionToCopy).get().get()
    } else {
        debugDb.collection(collectionToCopy).get().get()
    }

    val copyToDb = if (envTwo == "release") { releaseDb } else { debugDb }

    collectionOne.documents.forEach {
        val documentId = it.id
        val documentPath = "$collectionToCopyTo/$documentId"

        printGreen("Copying $documentPath to $envTwo db")

        copyToDb.collection(collectionToCopyTo).document(documentId).set(it.data).get()

        printGreen("Done copying $documentPath to $envTwo db")
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
