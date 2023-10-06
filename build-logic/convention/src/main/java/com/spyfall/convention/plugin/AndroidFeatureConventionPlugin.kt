package com.spyfall.convention.plugin

import com.spyfall.convention.extension.SpyfallExtension
import com.spyfall.convention.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * This plugin can be applied in a feature module in the build.gradle.kts file
 * using the following syntax
 * ```kotlin
 * plugins {
 *     id("spyfall.android.feature")
 * }
 * ```
 * feature modules should not depend on other feature modules directly, instead
 * they should depend on that feature modules api.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            if (extensions.findByName("spyfall") == null) {
                extensions.create("spyfall", SpyfallExtension::class.java)
            }

            pluginManager.apply {
                apply("spyfall.android.library")
            }

            // Libraries Shared Between All Features
            dependencies {
                add("implementation", libs.androidx.core)
                add("implementation",libs.androidx.lifecycle.ext)
                add("implementation",libs.androidx.lifecycle.vm)
                add("implementation",libs.androidx.fragment.ktx)
                add("implementation",libs.androidx.constraintlayout)
                add("implementation",libs.androidx.appcompat)
            }
        }
    }
}
