package com.spyfall.util

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

/**
 * Get the module or submodule as a [Dependency]
 */
internal fun DependencyHandler.getModule(name: String, submodule: String? = null) =
    project(":$name${submodule?.let { ":$submodule" } ?: ""}")

/**
 * Get the module or submodule as a [Dependency]
 */
internal fun DependencyHandler.feature(name: String) = project(":feature:$name")
