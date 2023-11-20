#!/usr/bin/env kotlin

@file:Repository("https://maven.google.com")
@file:Repository("https://repo1.maven.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:DependsOn("com.google.firebase:firebase-admin:9.1.1")
@file:DependsOn("com.google.gms:google-services:4.3.14")
@file:DependsOn("com.google.auth:google-auth-library-oauth2-http:1.14.0")

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.File
import java.io.FileInputStream
import java.util.Properties

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
if (isHelpCall || args.isEmpty()) {
    @Suppress("MaxLineLength")
    printRed(
        """
               This script creates a new app config of the provided version for the provided app. It copies the most 
               recent app config for that app. 
               
               our app configs are stored in a firestore /config collection were each document is a version of the app.
               and each document contains a map of values representing the configuration to be used. 
               
               Usage: ./create_app_config.main.kts [CONFIG_VERSION] 
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception(if (isHelpCall) "See Message Above" else "MUST PROVIDE ALL ARGUMENTS")
}

fun doWork() {
    val inputAppVersionName = args.getOrNull(0)
    val serviceAccountJsonFile = getServiceAccountJsonFile()
    val appVersion = inputAppVersionName ?: loadAppProperty("versionName")
    printGreen("Creating the app config for $ Version: $appVersion")

    if (!serviceAccountJsonFile.isFile) {
        printRed(
            """
                No service-account-key.json file was found.
                Please make sure you have added the file to the src/debug and src/release folders
                To get the file follow the instructions here: 
                https://firebase.google.com/docs/firestore/quickstart#initialize
                """.trimIndent()
        )
        @Suppress("TooGenericExceptionThrown")
        throw Exception("see message above")
    }

    val db = getDb(serviceAccountJsonFile.path)
    val mostRecentAppConfig = db.getMostPreviousAppConfigDocument(appVersion, "config-android")

    when (classifyAppConfigValidation(appVersion, db, "config-android")) {
        AppConfigValidation.AlreadyExists -> {
            printRed(
                """
                    The app config version name found was $appVersion.
                    This config already exists. Please make sure you input a new app version. or that the
                    new version name exists in "app.properties"
                    """.trimIndent()
            )
        }

        AppConfigValidation.InvalidFormat -> {
            printRed(
                """
                     The app config version name found was $appVersion.
                     This version is an invalid format. Please make sure the
                    correct format it input or exists in version name exists in "app.properties"
                    """.trimIndent()
            )
        }

        AppConfigValidation.Valid -> {
            val mostRecentConfigFields = mostRecentAppConfig?.get()?.get()?.data ?: mapOf()
            db.collection("config-android").document(appVersion).set(mostRecentConfigFields)
            printGreen("Config creation has finished.")
        }
    }
}

fun classifyAppConfigValidation(
    newConfigName: String,
    db: Firestore,
    configCollectionKey: String
): AppConfigValidation {
    return if (!newConfigName.isAppConfigFormat()) {
        AppConfigValidation.InvalidFormat
    } else if (
        db.collection(configCollectionKey)
            .listDocuments()
            .map { it.simpleName }
            .contains(newConfigName)
    ) {
        AppConfigValidation.AlreadyExists
    } else {
        AppConfigValidation.Valid
    }
}

enum class AppConfigValidation {
    Valid, AlreadyExists, InvalidFormat
}

fun getServiceAccountJsonFile() = File("app/src/release/service-account-key.json")

@Suppress("TooGenericExceptionCaught")
fun loadAppProperty(property: String): String = Properties().let {
    val file = File("app.properties")
    it.load(file.inputStream())
    @Suppress("SwallowedException")
    try {
        it.getProperty(property)
    } catch (e: NullPointerException) {
        @Suppress("TooGenericExceptionThrown")
        throw Error(
            """No app property found named: $property. 
                Please make sure this property is listed exactly as \"$property\" 
                in "app.properties """"".trimMargin()
        )
    }
}

@Suppress("TooGenericExceptionCaught")
fun loadConfigKey(): String = Properties().let {
    val file = File("gradle.properties")
    it.load(file.inputStream())
    @Suppress("SwallowedException")
    try {
        it.getProperty("com.spyfall.configCollectionKey")
    } catch (e: NullPointerException) {
        @Suppress("TooGenericExceptionThrown")
        throw Error(
            """Error getting the property named config-android """"".trimMargin()
        )
    }
}

@Suppress("TooGenericExceptionCaught")
fun getDb(serviceAccountJsonPath: String): Firestore {
    val serviceAccount = FileInputStream(serviceAccountJsonPath)
    val credentials = GoogleCredentials.fromStream(serviceAccount)

    val options = FirebaseOptions.builder().setCredentials(credentials).build()

    val app = try {
        println("Initializing Firebase app")
        FirebaseApp.initializeApp(options, "spyfall")
    } catch (e: IllegalStateException) {
        println("Firebase app already initialized. $e")
        null
    }
    return com.google.firebase.cloud.FirestoreClient.getFirestore(app)
}

fun Firestore.getMostPreviousAppConfigDocument(appVersion: String, configCollectionKey: String): DocumentReference? {
    val documents = collection(configCollectionKey).listDocuments().sortedWith { first, second ->
        val firstValues = first.simpleName.split(".")
        val secondValues = second.simpleName.split(".")
        return@sortedWith if (isFirstLarger(firstValues, secondValues)) 1 else -1
    }

    val indexOfNewAppVersion = getNewAppVersionPosition(documents, appVersion)
    return if (indexOfNewAppVersion <= 0) {
        printRed("No Previous App Config Found. $appVersion will be created but it will be blank.")
        null // this is likely the first app config being made in this case
    } else if (indexOfNewAppVersion >= documents.size) {
        documents.last() // this should not happen.
    } else {
        documents[indexOfNewAppVersion - 1]
    }
}

fun getNewAppVersionPosition(documents: List<DocumentReference>, appVersion: String): Int {
    val sortedDocumentNameList = documents
        .map { it.simpleName }
        .toMutableList()
        .apply { add(appVersion) }
        .sortedWith { first, second ->
            val firstValues = first.split(".")
            val secondValues = second.split(".")
            return@sortedWith if (isFirstLarger(firstValues, secondValues)) 1 else -1
        }
        .apply {
            val indexOfNewAppVersion = indexOf(appVersion)
            val mostPreviousAppVersion = getOrNull(indexOfNewAppVersion - 1)
            printGreen("Basing new config off of $mostPreviousAppVersion")
        }

    return sortedDocumentNameList.indexOf(appVersion)
}

val DocumentReference.simpleName: String
    get() = this.path.substringAfterLast("/")

fun isFirstLarger(first: List<String>, second: List<String>, currentIndex: Int = 0): Boolean {
    return if (first.size > currentIndex && second.size > currentIndex) {
        if (first[currentIndex].toInt() > second[currentIndex].toInt()) {
            true
        } else if (first[currentIndex].toInt() < second[currentIndex].toInt()) {
            false
        } else {
            isFirstLarger(first, second, currentIndex + 1)
        }
    } else first.size > currentIndex
}

fun String.isAppConfigFormat(): Boolean =
    this.contains(".") && this.replace(".", "").trim().toIntOrNull()?.let { true } ?: false


doWork()
