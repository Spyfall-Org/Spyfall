package com.spyfall.convention.shared

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.util.Properties

@Suppress("UnstableApiUsage")
fun Project.getLibVersion(name: String): String = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(name).get()
    .requiredVersion

/**
 * Get array of source path for all modules
 */
fun Project.getModuleSources(vararg excludeModules: String = emptyArray()): Array<String> {
    val sources = mutableListOf<String>()

    rootProject.subprojects.forEach { project ->
        if (!excludeModules.contains(project.name)) {
            val path = project.path.substring(1).replace(":", "/")
            sources.add("$path/src")
        }
    }

    return sources.toTypedArray()
}

fun Project.getVersionName(): String? =
    when (project.getProjectType()) {
        ProjectType.Spyfall -> loadAppVersionProperty("spyfall.versionName")
        ProjectType.Werewolf -> loadAppVersionProperty("werewolf.versionName")
        null -> null
    }


fun Project.getVersionCode(): Int? =
    when (project.getProjectType()) {
        ProjectType.Spyfall -> loadAppVersionProperty("spyfall.versionCode").toInt()
        ProjectType.Werewolf -> loadAppVersionProperty("werewolf.versionCode").toInt()
        null -> null
    }


@Suppress("TooGenericExceptionCaught")
fun Project.loadAppVersionProperty(property: String): String = Properties().let {
    val file = File(appVersionsPath)
    it.load(file.inputStream())
    @Suppress("SwallowedException")
    try {
        it.getProperty(property)
    } catch (e: NullPointerException) {
        @Suppress("TooGenericExceptionThrown")
        throw Error(
            """No app version property found named: $property. 
                Please make sure this property is listed exactly as \"$property\" 
                in $appVersionsPath""".trimMargin()
        )
    }
}

val Project.appVersionsPath: String
    get() = "$rootDir/app_versions.properties"

fun Project.getProjectType(): ProjectType? = when (project.name) {
    "spyfall" -> ProjectType.Spyfall
    "werewolf" -> ProjectType.Werewolf
    else -> null
}
