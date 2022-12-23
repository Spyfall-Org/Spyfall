package com.spyfall.convention.shared.task

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.spyfall.convention.shared.GREEN
import com.spyfall.convention.shared.RED
import com.spyfall.convention.shared.RESET
import com.spyfall.convention.shared.SharedConstants
import com.spyfall.convention.shared.getVersionName
import com.spyfall.convention.shared.printGreen
import com.spyfall.convention.shared.printRed
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.io.FileInputStream

/**
 * Gradle task to create a new app config based on the appVersionName passed in or based on the current app version
 *
 * you can run this task via:
 *
 * ./gradlew {APP_NAME}CreateAppConfig --appVersionName="{APP_VERSION_NAME}"
 *
 * ex: ./gradlew werewolfCreateAppConfig --appVersionName="1.2.3"
 */

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

    @set:Option(
        option = "appVersionName",
        description = "The App Version Name That You Would Like To Create A Config For."
    )
    @get:Input
    var inputAppVersionName: String? = null

    @TaskAction
    fun taskAction() {

        val appVersion = inputAppVersionName ?: project.getVersionName() ?: kotlin.run {
            printRed(
                """
                Version for project name ${projectName.get()} could not be found. 
                Please ensure you passed one in using -Pargs 
                or that the CreateAppConfigTask.kt script has access to the app version
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
            return@sortedWith if (isFirstLarger(firstValues, secondValues)) 1 else -1
        }

        val indexOfNewAppVersion = getNewAppVersionPosition(documents, appVersion)
        return if (indexOfNewAppVersion <= 0) {
            printRed("NO PREVIOUS MOST APP CONFIG FOUND")
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
                return@sortedWith if (isFirstLarger(firstValues, secondValues)) 1 else -1
            }
            .apply {
                printRed("Sorted list of documents after adding new one is $this")
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

    @Suppress("TooGenericExceptionCaught")
    private fun getDb(serviceAccountJsonPath: String, appName: String): Firestore {
        val serviceAccount = FileInputStream(serviceAccountJsonPath)
        val credentials = GoogleCredentials.fromStream(serviceAccount)

        val options = FirebaseOptions.builder().setCredentials(credentials).build()

        val app = try {
            println("Initializing Firebase app")
            FirebaseApp.initializeApp(options, appName)
        } catch (e: IllegalStateException) {
            println("Firebase app already initialized. $e")
            null
        }
        return FirestoreClient.getFirestore(app)
    }
}


