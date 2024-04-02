package com.spyfall.plugin

import com.spyfall.extension.JavaExtension
import com.spyfall.util.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            if (extensions.findByName("oddOneOut") == null) {
                extensions.create("oddOneOut", JavaExtension::class.java)
            }

            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureKotlinJvm()
        }
    }
}
