#!/usr/bin/env kotlin

@file:Repository("https://maven.google.com")
@file:Repository("https://repo1.maven.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("com.google.firebase:firebase-admin:9.1.1")
@file:DependsOn("com.google.gms:google-services:4.3.14")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FieldPath
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream
import java.util.UUID

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isHelpCall = args.isEmpty() || (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall) {
    @Suppress("MaxLineLength")
    printRed(
        """
               This script adds a player of the provided name to the debug game with the provided access code.      
               Usage: ./add_player_debug.main.kts [ACCESS_CODE] [NAME]
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {

    val accessCode = args[0]
    val playerName = args[1]
    val playerId = UUID.randomUUID().toString()
    val debugServiceAccountJsonFile = File("app/src/debug/service-account-key.json")

    if (!debugServiceAccountJsonFile.isFile) {
        printRed(
            """
                one of the service-account-key.json files was not found.
                please run ./scripts/get_secret_files.main.kts to get all the secret files.
            """.trimIndent()
        )
        return
    }

    val debugDb = getDb(debugServiceAccountJsonFile.absolutePath)

    printGreen("Adding Player")

    debugDb
        .collection("games")
        .document(accessCode)
        .update(
            FieldPath.of("players", playerId), mapOf(
                "id" to playerId,
                "role" to null,
                "userName" to playerName,
                "isHost" to false,
                "isOddOneOut" to false,
                "votedCorrectly" to null
            )
        )
        .get()

    printGreen("Done adding $playerName to game $accessCode")
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
