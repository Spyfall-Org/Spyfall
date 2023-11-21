package com.spyfall.convention.extension


import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.spyfall.convention.util.SharedConstants
import com.spyfall.convention.util.configureAndroidCompose
import com.spyfall.convention.util.getModule
import com.spyfall.convention.util.libs
import com.spyfall.convention.util.optInKotlinMarkers
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import javax.inject.Inject

@SpyfallExtensionDsl
abstract class SpyfallFeatureExtension {
    @get:Inject
    internal abstract val project: Project

    fun optIn(vararg markerClasses: String) {
        project.optInKotlinMarkers(*markerClasses)
    }

    fun daggerHilt(withProcessors: Boolean = true) {
        if (withProcessors) {
            project.pluginManager.apply("dagger.hilt.android.plugin")
            kapt {
                arguments {
                    arg("dagger.fastInit", "enabled")
                }
            }
        }

        project.dependencies {
            "implementation"(project.libs.dagger)
            "implementation"(project.libs.dagger.hilt.android)
            "implementation"(project.libs.dagger.hilt.core)
            "implementation"(project.libs.autoDagger.core)
            if (withProcessors) {
                val configuration = "kapt"
                configuration(project.libs.dagger.compiler)
                configuration(project.libs.dagger.hilt.compiler)
                configuration(project.libs.autoDagger.compiler)
            }
        }
    }

    fun compose() {
        val projectExt = project.extensions.findByType(LibraryExtension::class.java)
            ?: project.extensions.findByType(ApplicationExtension::class.java)
            ?: error(
                """
                Attempted to use compose outside of Application or Library project
                make sure you've applied one of the following plugin to the calling projects build.gradle:
                
                id("spyfall.android.application")
                id("spyfall.android.library")
                id("spyfall.android.feature")

            """.trimIndent()
            )

        project.configureAndroidCompose(projectExt)
    }

    fun flowroutines() {
        project.dependencies {
            add("implementation", getModule("libraries:flowroutines"))
        }
    }

    fun firebase() {
        project.dependencies {
            "implementation"(platform(project.libs.firebase.bom))
            add("implementation", project.libs.firebase.database)
            add("implementation", project.libs.firebase.firestore)
            add("implementation", project.libs.firebase.storage)
            add("implementation", project.libs.firebase.database)
            add("implementation", project.libs.kotlinx.coroutines.play.services)
        }
    }

    fun kapt(configure: KaptExtension.() -> Unit = {}) {
        project.pluginManager.apply("kotlin-kapt")
        project.extensions.configure(configure)
    }

    fun unitTesting() {
        with(project.pluginManager) {
            apply("com.android.test")
            apply("org.jetbrains.kotlin.android")
        }

        project.extensions.configure<TestExtension> {
            defaultConfig.targetSdk = SharedConstants.testingTargetSdk
        }
    }

    fun networking() {
        TODO("Not yet implemented")
    }
}
