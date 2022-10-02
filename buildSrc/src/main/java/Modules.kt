import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

/**
 * Get the module or submodule as a [Dependency]
 *
 * Submodules are prefixed with the parent name to ensure they are unique
 * until this issue is resolved -> https://issuetracker.google.com/issues/144668323
 */
fun DependencyHandler.getModule(name: String, submodule: String? = null) =
    project(":$name${submodule?.let { ":$name-$submodule" } ?: ""}")