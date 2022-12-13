package com.spyfall.convention.shared

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.spyfall.convention.shared.spyfall.SpyfallConstants
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream

/**
 * uses the projects app version to create a config for that version.
 * After uploading an app version we increment the number and start developing for the next version
 * upon doing that we create the config for that new version by running this task
 *
 * you can run this task via: {APP_NAME}CreateAppConfig
 * ./gradlew werewolfCreateAppConfig
 * ./gradlew spyfallCreateAppConfig
 *
 */

const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val RESET = "\u001b[0m"

fun printRed(text: String) {
    println(RED + text + RESET)
}

fun printGreen(text: String) {
    println(GREEN + text + RESET)
}

internal fun Project.configureAppConfigCreationTask() {
    val name = this.projectDir.name
    val serviceAccountFile = this.file("service-account-key.json")

    tasks.register(
        "${this.projectDir.name}CreateAppConfig", CreateAppConfigTask::class.java
    ) {
        projectName.set(name)
        serviceAccountJsonFile.set(serviceAccountFile)
    }
}

internal abstract class CreateAppConfigTask : DefaultTask() {

    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val serviceAccountJsonFile: Property<File>

    @TaskAction
    fun taskAction() {

        val appVersion = getAppVersion(projectName.get()) ?: kotlin.run {
            printRed(
                """
                Version for project name ${projectName.get()} could not be found
                """.trimIndent()
            )
            return
        }

        if (!serviceAccountJsonFile.get().isFile) {
            printRed(
                """
                No service-account-key.json file was found.
                 Please make sure you have added the file to the app project root.
                To get the file follow the instructions here: https://firebase.google.com/docs/firestore/quickstart#initialize
                """.trimIndent()
            )
            return
        }

        val db = getDb(serviceAccountJsonFile.get().path, projectName.get())
        val mostRecentAppConfig = db.getMostPreviousAppConfigDocument(appVersion)

        when (classifyAppConfigValidation(appVersion, db)) {
            AppConfigValidation.AlreadyExists -> {
                printRed(
                    """
                    The app config version name found was $appVersion.
                    This config already exists. Please make sure the
                    correct version name exists in "build-logic/{APP_NAME}Constants"
                    """.trimIndent()
                )
            }

            AppConfigValidation.InvalidFormat -> {
                printRed(
                    """
                     The app config version name found was $appVersion.
                     This version is an invalid format. Please make sure the
                    correct format version name exists in "build-logic/{APP_NAME}Constants"
                    """.trimIndent()
                )
            }

            AppConfigValidation.Valid -> {
                printGreen(" Creating the app config for version: $appVersion.")
                val mostRecentConfigFields = mostRecentAppConfig?.get()?.get()?.data ?: mapOf()
                db.collection(SharedConstants.configCollection).document(appVersion).set(mostRecentConfigFields)
            }
        }
    }

    private fun getAppVersion(projectName: String): String? =
        when (projectName) {
            "spyfall" -> SpyfallConstants.versionName
            "werewolf" -> WerewolfConstants.versionName
            else -> null
        }

    private fun classifyAppConfigValidation(
        newConfigName: String,
        db: Firestore
    ): AppConfigValidation {
        return if (!newConfigName.isAppConfigFormat()) {
            AppConfigValidation.InvalidFormat
        } else if (
            db.collection(SharedConstants.configCollection)
                .listDocuments()
                .map { it.simpleName }
                .contains(newConfigName)
        ) {
            AppConfigValidation.AlreadyExists
        } else {
            AppConfigValidation.Valid
        }
    }

    sealed class AppConfigValidation {
        object Valid : AppConfigValidation()
        object AlreadyExists : AppConfigValidation()
        object InvalidFormat : AppConfigValidation()
    }

    private val DocumentReference.simpleName: String
        get() = this.path.substringAfterLast("/")

    private fun Firestore.getMostPreviousAppConfigDocument(appVersion: String): DocumentReference? {
        val documents = collection(SharedConstants.configCollection).listDocuments().sortedWith { first, second ->
            val firstValues = first.simpleName.split(".")
            val secondValues = second.simpleName.split(".")
            return@sortedWith if (isFirstLarger(firstValues, secondValues)) -1 else 1
        }

        val indexOfNewAppVersion = getNewAppVersionPosition(documents, appVersion)
        return if (indexOfNewAppVersion == 0) {
            null // this is likely the first app config being made in this case
        } else if (indexOfNewAppVersion >= documents.size) {
            documents.last() // this should not happen.
        } else {
            documents[indexOfNewAppVersion - 1]
        }
    }

    private fun getNewAppVersionPosition(documents: List<DocumentReference>, appVersion: String): Int {
        val sortedDocumentNameList = documents
            .map { it.simpleName }
            .toMutableList()
            .apply { add(appVersion) }
            .sortedWith { first, second ->
                val firstValues = first.split(".")
                val secondValues = second.split(".")
                return@sortedWith if (isFirstLarger(firstValues, secondValues)) -1 else 1
            }

        return sortedDocumentNameList.indexOf(appVersion)
    }

    private fun isFirstLarger(first: List<String>, second: List<String>, currentIndex: Int = 0): Boolean {
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

    private fun String.isAppConfigFormat(): Boolean =
        this.contains(".") && this.replace(".", "").trim().toIntOrNull()?.let { true } ?: false

    private fun getDb(serviceAccountJsonPath: String, appName: String): Firestore {
        val serviceAccount = FileInputStream(serviceAccountJsonPath)
        val credentials = GoogleCredentials.fromStream(serviceAccount)

        val options = FirebaseOptions.builder().setCredentials(credentials).build()

        val app = try {
            println("Initializing Firebase app")
            FirebaseApp.initializeApp(options, appName)
        } catch(e: IllegalStateException) {
            println("Firebase app already initialized")
            null
        }
        return FirestoreClient.getFirestore(app)
    }
}
