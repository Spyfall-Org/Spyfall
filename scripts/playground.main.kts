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
import java.util.UUID
import kotlin.script.experimental.jvm.impl.scriptMetadataPath

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

    val releaseDb = getDb(releaseServiceAccountJsonFile.absolutePath)
    val debugDb = getDb(debugServiceAccountJsonFile.absolutePath)

    val topCelebrities = listOf(
        "Brad Pitt",
        "Beyoncé",
        "Leonardo DiCaprio",
        "Taylor Swift",
        "Tom Cruise",
        "Oprah Winfrey",
        "Dwayne \"The Rock\" Johnson",
        "Angelina Jolie",
        "Robert Downey Jr.",
        "Ariana Grande",
        "Will Smith",
        "Kim Kardashian",
        "Jennifer Lawrence",
        "Drake",
        "Scarlett Johansson",
        "LeBron James",
        "Lady Gaga",
        "Chris Hemsworth",
        "Johnny Depp",
        "Kanye West",
        "Rihanna",
        "Emma Watson",
        "George Clooney",
        "Selena Gomez",
        "Justin Bieber",
        "Jennifer Aniston",
        "Elon Musk",
        "Billie Eilish",
        "Ed Sheeran",
        "Katy Perry",
        "Chris Evans",
        "Cardi B",
        "Margot Robbie",
        "Bruno Mars",
        "Tom Hanks",
        "Miley Cyrus",
        "Kylie Jenner",
        "Gal Gadot",
        "Shawn Mendes",
        "Priyanka Chopra",
        "Zendaya",
        "Mark Zuckerberg",
        "Jennifer Lopez",
        "Ellen DeGeneres",
        "Chris Pratt",
        "Adele",
        "Serena Williams",
        "Celine Dion"
    )

    val documents = debugDb
        .collection("versioned-packs")
        .document("1")
        .collection("languages")
        .get()
        .get()
        .documents

    documents.forEach {
        val packs = it.data["packs"] as MutableList<Map<String, Any>>
        packs.add(
            mapOf(
                "id" to UUID.randomUUID().toString(),
                "name" to "Celebrities",
                "type" to "celebrity",
                "celebrities" to topCelebrities
            )
        )

        it.reference.update("packs", packs)
    }

    printGreen("Done.")
}

data class Pack(
    val id: String,
    val name: String,
    val type: String,
    val items: List<String>
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
