package com.spyfall.convention.util

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
        }

        dependencies {
            val bom = libs.androidx.compose.bom
            add("implementation", platform(bom))
            add("implementation", libs.androidx.compose.runtime)
            add("implementation",libs.androidx.navigation.runtime.ktx)
            add("implementation",libs.hilt.navigation.compose)
            add("implementation",libs.androidx.navigation.compose)
            add("androidTestImplementation", platform(bom))
            // Add ComponentActivity to debug manifest
            add("debugImplementation", libs.androidx.compose.ui.testManifest)
            add("debugImplementation",libs.androidx.compose.ui.tooling)
            // Screenshot Tests on JVM
            add("testImplementation", libs.robolectric)
            add("testImplementation", libs.roborazzi)

            add("implementation", libs.androidx.compose.foundation)
            add("implementation", libs.androidx.compose.foundation.layout)
            add("implementation", libs.androidx.compose.runtime.livedata)
            add("implementation", libs.androidx.compose.ui.tooling.preview)
            add("implementation", libs.androidx.compose.ui.util)
        }

        testOptions {
            unitTests {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }
    }
}
