package com.spyfall.plugin
import com.spyfall.util.BuildEnvironment
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("detektPlugins", libs.findLibrary("detekt.formatting").get())
            }

            tasks.withType<Detekt>().configureEach {
                setupCommonDetektSettings()
            }

            /**
             * Runs detekt for all Kotlin files.
             */
            @Suppress("SpreadOperator")
            if (rootProject.tasks.findByName("detektAll") == null) {
                rootProject.tasks.register<Detekt>("detektAll") {
                    group = "verification"
                    description = "Check Kotlin code style for all files."
                    setupCommonDetektSettings()
                }
            }
        }
    }
}

fun Detekt.setupCommonDetektSettings() {
    // Common properties
    parallel = true
    autoCorrect = !BuildEnvironment.isCIBuild
    buildUponDefaultConfig = false
    ignoreFailures = BuildEnvironment.isCIBuild
    jvmTarget = "1.8"

    // Setup sources for run
    config.from(project.file("${project.rootDir}/config/detekt/detekt.yml"))
    baseline.set(project.file("${project.rootDir}/config/detekt/detekt-baseline.xml"))

    source(project.file(project.projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")

    // reports configuration
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(false)

        html.outputLocation.set(project.file("build/reports/codestyle/detekt.html"))
        xml.outputLocation.set(project.file("build/reports/codestyle/detekt.xml"))
    }
}
