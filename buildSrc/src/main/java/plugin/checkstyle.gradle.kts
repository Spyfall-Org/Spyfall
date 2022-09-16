package plugin

import extension.getModuleSources
import analysis.CodeAnalysis

plugins {
    checkstyle
}

checkstyle {
    toolVersion = "8.13"
    isIgnoreFailures = false
    maxWarnings = 0
}

@Suppress("SpreadOperator")
tasks.withType(Checkstyle::class) {
    classpath = files()
    exclude(*CodeAnalysis.excludes)

    reports {
        html.required.set(true)
        html.outputLocation.set(file("${CodeAnalysis.reportPath}/checkstyle.html"))
        xml.required.set(true)
        xml.outputLocation.set(file("${CodeAnalysis.reportPath}/checkstyle.xml"))
    }
}

/**
 * Runs checkstyle for all Java and XML files.
 * Kotlin files are not supported by Checkstyle.
 */
tasks.register<Checkstyle>("checkstyleAll") {
    group = "verification"
    description = "Check Java code style for all files."
    source(project.getModuleSources("test"))
    include("**/java/**/*.java", "**/res/**/*.xml")
}