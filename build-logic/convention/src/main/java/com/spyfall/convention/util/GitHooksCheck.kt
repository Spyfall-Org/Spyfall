package com.spyfall.convention.util

import org.gradle.api.Project

internal fun Project.configureGitHooksCheck() {

    fun checkHooksInstalled() {
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

    project.gradle.projectsEvaluated {
        checkHooksInstalled()
    }
}
