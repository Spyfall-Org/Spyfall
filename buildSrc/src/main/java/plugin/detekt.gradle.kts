package plugin

import analysis.CodeAnalysis
import extension.detektVersion
import extension.getModuleSources
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    toolVersion = detektVersion
    config = files("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/detekt-baseline.xml")
    autoCorrect = true
    parallel = true

    reports {
        sarif.enabled = false
        txt.enabled = false
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<Detekt>().configureEach {
    config.from(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
    autoCorrect = true
    jvmTarget = "1.8"

    reports {
        sarif.enabled = false
        txt.enabled = false
    }
}

/**
 * Runs detekt for all Kotlin files.
 */
@Suppress("SpreadOperator")
if (rootProject.tasks.findByName("detektAll") == null) {
    rootProject.tasks.register<Detekt>("detektAll") {
        group = "verification"
        description = "Check Kotlin code style for all files."
        source(project.getModuleSources(), "buildSrc")
        include("**/java/**/*.kt", "**/kotlin/**", "*.kts")
        exclude(*CodeAnalysis.excludes)

        reports {
            html.destination = file("${CodeAnalysis.reportPath}/detekt.html")
            xml.destination = file("${CodeAnalysis.reportPath}/detekt.xml")
        }
    }
}

val detektProjectBaseline by tasks.registering(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/buildSrc/**")
    exclude("**/test/**/*.kt")
}
