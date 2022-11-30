package com.spyfall.convention.shared

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension

enum class SpyfallFlavorDimension {
    ProjectStatus
}

enum class SpyfallFlavor(val dimension: SpyfallFlavorDimension, val orientation: String) {
    Legacy(SpyfallFlavorDimension.ProjectStatus, "portrait"),
    Refactor(SpyfallFlavorDimension.ProjectStatus, "unspecified")
}

fun configureSpyfallFlavors(
    commonExtension: CommonExtension<*, *, *, *>
) {
    commonExtension.apply {
        flavorDimensions += SpyfallFlavorDimension.ProjectStatus.name
        productFlavors {
            SpyfallFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        this.manifestPlaceholders["screenOrientation"] = it.orientation
                    }
                }
            }
        }
    }
}
