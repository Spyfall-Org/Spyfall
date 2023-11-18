package com.spyfall.convention.extension


import com.spyfall.convention.util.getModule
import com.spyfall.convention.util.libs
import com.spyfall.convention.util.optInKotlinMarkers
import com.spyfall.convention.util.useKspDagger
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import javax.inject.Inject

@SpyfallExtensionDsl
abstract class SpyfallJavaExtention {
    @get:Inject
    internal abstract val project: Project

    fun optIn(vararg markerClasses: String) {
        project.optInKotlinMarkers(*markerClasses)
    }

    fun daggerHilt(withProcessors: Boolean = true) {
        if (withProcessors) {

                kapt {
                    arguments {
                        arg("dagger.fastInit", "enabled")
                    }
                }

        }

        project.dependencies {
            "implementation"(project.libs.dagger)
            "implementation"(project.libs.dagger.hilt.core)
            "implementation"(project.libs.autoDagger.core)
            if (withProcessors) {
                val configuration =
                    "kapt"

                configuration(project.libs.dagger.compiler)
                configuration(project.libs.dagger.hilt.compiler)
                configuration(project.libs.autoDagger.compiler)
            }
        }
    }

    fun flowroutines() {
        project.dependencies {
            add("implementation", getModule("libraries:coreFlowroutines"))
        }
    }

    fun firebase() {
        project.dependencies {
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
}
