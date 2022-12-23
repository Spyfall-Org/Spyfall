package com.spyfall.convention.shared

sealed class ProjectType(val projectName: String) {
    object Spyfall : ProjectType("spyfall")
    object Werewolf : ProjectType("spyfall")
}
