package com.spyfall.convention.util

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.`is`
import java.io.File
import java.lang.System.getenv

internal fun Project.checkForAppModuleSecretFiles() {

    val secretFiledNeeded = listOf(
        File("${project.projectDir}/src/release/google-services.json"),
        File("${project.projectDir}/src/release/service-account-key.json"),
        File("${project.projectDir}/src/debug/google-services.json"),
        File("${project.projectDir}/src/debug/service-account-key.json")
    )

    fun isAppMissingSecretFiles(): Boolean = secretFiledNeeded.any { !it.isFile }

    fun installSecretFiles() {

        val serviceKeyPath = getenv("SPYFALL_SERVICE_KEY_PATH")
            ?: "${project.rootDir}/service_key.json".takeIf { File(it).isFile }
            ?: "${project.rootDir}/app/service_key.json".takeIf { File(it).isFile }

        if (serviceKeyPath == null || !File(serviceKeyPath).isFile) {
            @Suppress("UseCheckOrError")
            throw IllegalStateException(
                """
                   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    
                    Could not find the service_key.json needed to download secret files from google drive. 
                    Please download the service_key.json here: 
                    https://drive.google.com/file/d/1t456fo07BN9NF0a3e1Ds9KNBccV1X1AQ/view?usp=share_link
                    
                    Once the file is saved, 
                    you can either
                    1. move the file to the `app` folder
                    
                    2. copy the path and add the following line to your ~/.bashrc 
                    or ~/.zshrc (depending on your set up)
                    `export SPYFALL_SERVICE_KEY_PATH="INSERT_PATH"`
                    
                    \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

                """.trimIndent()
            )
        } else {
            val result = ProcessBuilder("./scripts/get_secret_files.main.kts", serviceKeyPath)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()

            @Suppress("TooGenericExceptionThrown")
            if (result != 0) {
                @Suppress("TooGenericExceptionThrown")
                throw Exception(
                    """
                   Failed to run ./scripts/get_secret_files.main.kts with input "$serviceKeyPath"
                   Please see AppModuleSecretFilesCheck.kt
                """.trimIndent()
                )
            } else {
                printGreen("Finished downloading all secret files")
            }
        }
    }

    if (isAppMissingSecretFiles()) {
        printRed(
            "MISSING SECRET FILES FOR PROJECT ${project.name.toUpperCase()}. " +
                    "\nUPDATING ALL SECRET FILES."
        )
        installSecretFiles()
    } else {
        printGreen("Project ${project.name} has all Secret files needed.")
    }
}
