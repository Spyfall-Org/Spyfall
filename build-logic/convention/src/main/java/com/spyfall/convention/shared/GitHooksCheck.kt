package com.spyfall.convention.shared

import com.spyfall.convention.shared.task.printGreen
import org.gradle.api.Project

internal fun Project.configureGitHooksCheck() {

    fun checkHooksInstalled() {
        project.rootProject.file("config/git-hooks")
            .listFiles()
            .forEach {
                printGreen("Searching for ${it.name} in the .git/hooks folder")
                val installedGitHook = project.rootProject.file(".git/hooks/${it.name}")

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
