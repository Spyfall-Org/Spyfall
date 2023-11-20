package com.spyfall.convention.plugin

import com.android.build.gradle.LibraryExtension
import com.spyfall.convention.extension.SpyfallFeatureExtension
import com.spyfall.convention.util.SharedConstants
import com.spyfall.convention.util.configureKotlinAndroid
import com.spyfall.convention.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * This plugin can be applied in a library module in the build.gradle.kts file
 * using the following syntax
 * ```kotlin
 * plugins {
 *     id("spyfall.android.library")
 * }
 * ```
 * library modules should not depend on android specific frameworks.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            if (extensions.findByName("spyfall") == null) {
                extensions.create("spyfall", SpyfallFeatureExtension::class.java)
            }

            /*
            TODO
            not all libraries should automatically get the android stuff, lets consider
            making things different
             */
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = SharedConstants.targetSdk
            }

            dependencies {
                add("implementation", libs.timber)
            }
        }
    }
}
