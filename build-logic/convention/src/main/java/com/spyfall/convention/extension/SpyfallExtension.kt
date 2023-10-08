package com.spyfall.convention.extension


import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.spyfall.convention.util.SharedConstants
import com.spyfall.convention.util.commonExt
import com.spyfall.convention.util.configureAndroidCompose
import com.spyfall.convention.util.libs
import com.spyfall.convention.util.optInKotlinMarkers
import com.spyfall.convention.util.useKspDagger
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import javax.inject.Inject

@SpyfallExtensionDsl
abstract class SpyfallExtension {
    @get:Inject
    internal abstract val project: Project

    //var namespace: String? by project.commonExt::namespace

    fun optIn(vararg markerClasses: String) {
        project.optInKotlinMarkers(*markerClasses)
    }

    fun daggerAndroid() {
        project.pluginManager.apply("com.google.dagger.hilt.android")
    }

    fun daggerHilt(withProcessors: Boolean) {

        if (withProcessors) {
            if (project.useKspDagger) {
                ksp {
                    arg("dagger.fastInit", "enabled")
                }
            } else {
                kapt {
                    arguments {
                        arg("dagger.fastInit", "enabled")
                    }
                }
            }
        }

        project.dependencies {
            "implementation"(project.libs.hilt.android)

            if (withProcessors) {
                val configuration = if (project.useKspDagger) {
                    "ksp"
                } else {
                    "kapt"
                }

                configuration(project.libs.hilt.compiler)
            }
        }
    }

    fun compose() {

        val projectExt = project.extensions.findByType(LibraryExtension::class.java)
            ?: project.extensions.findByType(ApplicationExtension::class.java)
            ?: error("""
                Attempted to use compose outside of Application or Library project
                make sure you've applied one of the following plugin to the calling projects build.gradle:
                
                id("spyfall.android.application")
                id("spyfall.android.library")
                id("spyfall.android.feature")

            """.trimIndent())

        project.configureAndroidCompose(projectExt)
    }

    fun flowroutines() {
        project.dependencies {
            add("implementation", project.libs.kotlinx.coroutines)
        }
    }

    fun kapt(configure: KaptExtension.() -> Unit = {}) {
        project.pluginManager.apply("kotlin-kapt")
        project.extensions.configure(configure)
    }

    fun ksp(configure: KspExtension.() -> Unit = {}) {
        project.pluginManager.apply("com.google.devtools.ksp")
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
