package com.spyfall.convention.plugin

import com.spyfall.convention.extension.SpyfallJavaExtention
import com.spyfall.convention.util.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            if (extensions.findByName("spyfall") == null) {
                extensions.create("spyfall", SpyfallJavaExtention::class.java)
            }

            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            configureKotlinJvm()
        }
    }
}
