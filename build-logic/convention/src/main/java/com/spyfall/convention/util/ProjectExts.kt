package com.spyfall.convention.util

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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

fun Project.getVersionName(): String = loadAppProperty("versionName")

fun Project.getVersionCode(): Int =loadAppProperty("versionCode").toInt()

fun Project.getPackageName(): String = loadAppProperty("packageName")

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

internal val Project.useKspDagger
    get() = providers.gradleProperty("spyfall.useKspDagger").orNull?.toBooleanStrict() == true

internal val Project.libs get() = the<LibrariesForLibs>()

internal fun Project.optInKotlinMarkers(vararg markerClasses: String) {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += markerClasses.map { "-opt-in=$it" }
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
