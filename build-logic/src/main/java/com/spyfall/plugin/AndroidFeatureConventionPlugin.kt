package com.spyfall.plugin

import com.spyfall.extension.FeatureExtension
import com.spyfall.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * This plugin can be applied in a feature module in the build.gradle.kts file
 * using the following syntax
 * ```kotlin
 * plugins {
 *     id("ooo.android.feature")
 * }
 * ```
 * feature modules should not depend on other feature modules impls but rather on their
 * public modules
 *
 * ex:
 * DONT DO: implementation(project(":feature:feature1:impl"))
 * DO: implementation(project(":feature:feature1"))
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            // TODO cleanup truly need tp update the spyfall ext to be split into
            // what type of module it is. I could also do some huge cleanup on this module
            if (extensions.findByName("oddOneOut") == null) {
                extensions.create("oddOneOut", FeatureExtension::class.java)
            }

            pluginManager.apply {
                apply("ooo.android.library")
                apply("kotlin-parcelize")
            }

            // Libraries Shared Between All Features
            dependencies {
                add("implementation", libs.timber)
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
