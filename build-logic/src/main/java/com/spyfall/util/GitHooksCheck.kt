package com.spyfall.util

import org.gradle.api.Project

/**
 * Checks if the project has the required git hooks installed. If not it will throw an exception,
 * failing the build.
 *
 * We do this to ensure that contributors have the safeguard git hooks before beginning work.
 */
internal fun Project.configureGitHooksCheck() {
    project.rootProject.file("config/git-hooks")
        .listFiles()
        ?.forEach {
            val installedGitHook = project.rootProject.file(".git/hooks/${it.nameWithoutExtension}")

            if (!installedGitHook.isFile && !BuildEnvironment.isCIBuild) {
                throw IllegalStateException(
                    "The project requires a ${installedGitHook.name} " +
                            "git hook to be installed. Either run `./scripts/install-git-hooks.sh` to " +
                            "install the default git hooks or install your own. Check the GitHooksCheck" +
                            "for more info"
                )
            }
        }
}
