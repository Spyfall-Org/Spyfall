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
        ProjectType.Spyfall -> loadAppProperty("spyfall.versionName")
        ProjectType.Werewolf -> loadAppProperty("werewolf.versionName")
        null -> null
    }


fun Project.getVersionCode(): Int? =
    when (project.getProjectType()) {
        ProjectType.Spyfall -> loadAppProperty("spyfall.versionCode").toInt()
        ProjectType.Werewolf -> loadAppProperty("werewolf.versionCode").toInt()
        null -> null
    }

fun Project.getPackageName(): String? =
    when (project.getProjectType()) {
        ProjectType.Spyfall -> loadAppProperty("spyfall.packageName")
        ProjectType.Werewolf -> loadAppProperty("werewolf.packageName")
        null -> null
    }


@Suppress("TooGenericExceptionCaught")
fun Project.loadAppProperty(property: String): String = Properties().let {
    val file = File(appPropertiesPath)
    it.load(file.inputStream())
    @Suppress("SwallowedException")
    try {
        it.getProperty(property)
    } catch (e: NullPointerException) {
        @Suppress("TooGenericExceptionThrown")
        throw Error(
            """No app property found named: $property. 
                Please make sure this property is listed exactly as \"$property\" 
                in $appPropertiesPath""".trimMargin()
        )
    }
}

@Suppress("TooGenericExceptionCaught")
fun Project.loadGradleProperty(property: String): String? = Properties().let {
    val file = File(gradlePropertyPath)
    it.load(file.inputStream())
    runCatching { it.getProperty(property) }.getOrNull()
}

val Project.appPropertiesPath: String
    get() = "$rootDir/app.properties"

val Project.gradlePropertyPath: String
    get() = "$rootDir/gradle.properties"

fun Project.getProjectType(): ProjectType? = when (project.name) {
    "spyfall" -> ProjectType.Spyfall
    "werewolf" -> ProjectType.Werewolf
    else -> null
}
