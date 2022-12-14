package com.spyfall.convention.shared

import com.android.build.api.dsl.VariantDimension
import com.spyfall.convention.shared.spyfall.SpyfallConstants
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

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

/**
 * Simplify adding BuildConfig fields to build variants
 */
@Suppress("UnstableApiUsage")
fun VariantDimension.buildConfigField(name: String, value: Any?) {
    when (value) {
        null -> buildConfigField("String", name, "null")
        is String -> buildConfigField("String", name, "\"$value\"")
        is Boolean -> buildConfigField("boolean", name, value.toString())
        is Int -> buildConfigField("int", name, value.toString())
        is Provider<*> -> buildConfigField(name, value.get())
        else -> throw IllegalArgumentException("Unknown type for $value")
    }
}



fun Project.getVersionName(): String? =
    when (this.projectDir.name) {
        "spyfall" -> SpyfallConstants.versionName
        "werewolf" -> WerewolfConstants.versionName
        else -> null
    }

const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val RESET = "\u001b[0m"
