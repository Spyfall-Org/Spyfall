package com.dangerfield.features.qa.internal

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
            value = true,
            description = null
        ),
        QaViewModel.DisplayableConfigValue(
            name = "Some Other Name",
            path = "some_path",
            value = 9,
            description = null
        )
    )

    val consentExperiments = listOf(
        QaViewModel.DisplayableExperiment(
            name = "Force Some Consent Thing",
            path = "some_path",
            value =  false,
            description = "Some description",
            isDebugOnly = true
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