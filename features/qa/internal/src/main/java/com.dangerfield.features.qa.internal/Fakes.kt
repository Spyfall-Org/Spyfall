package com.dangerfield.features.qa.internal

import com.dangerfield.libraries.config.ConfiguredValue
import com.dangerfield.libraries.config.Experiment

object Fakes {
    val displayableConfigValues = listOf(
        QaViewModel.DisplayableConfigValue(
            name = "Some Name",
            path = "some_path",
            value = "Some value",
            description = "Some description"
        ),
        QaViewModel.DisplayableConfigValue(
            name = "Some Other Name",
            path = "some_path",
            value = false,
            description = null
        ),
        QaViewModel.DisplayableConfigValue(
            name = "Some Other Name",
            path = "some_path",
            value = 9,
            description = null
        )
    )

    val displayableExperiments = listOf(
        QaViewModel.DisplayableExperiment(
            name = "Some Name",
            path = "some_path",
            value = "Some value",
            description = "Some description",
            isDebugOnly = true
        ),
        QaViewModel.DisplayableExperiment(
            name = "Some Other Name",
            path = "some_path",
            value = false,
            description = null,
            isDebugOnly = false

        ),
        QaViewModel.DisplayableExperiment(
            name = "Some Other Name",
            path = "some_path",
            value = 9,
            description = null,
            isDebugOnly = false
        )
    )
}