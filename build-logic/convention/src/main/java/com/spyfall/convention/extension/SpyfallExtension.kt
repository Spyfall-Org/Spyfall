package com.spyfall.convention.extension


import com.android.build.gradle.TestExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.spyfall.convention.util.SharedConstants
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
