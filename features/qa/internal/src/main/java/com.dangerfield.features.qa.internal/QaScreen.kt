package com.dangerfield.features.qa.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableConfigValue
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableExperiment
import com.dangerfield.features.qa.internal.item.QASwitchItem
import com.dangerfield.features.qa.internal.item.QaInfoItem
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.qa.internal.item.QaActionItem
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text

// TODO add support for devs to manually add their own QA items
@Composable
fun QaScreen(
    configuredValues: List<DisplayableConfigValue>,
    experiments: List<DisplayableExperiment>,
    consentExperiments: List<DisplayableExperiment>,
    modifier: Modifier = Modifier,
    sessionId: String? = null,
    languageCode: String,
    onResetConsent: () -> Unit,
    onExperimentOverride: (DisplayableExperiment, Any) -> Unit = { _, _ -> },
    onConfigValueOverride: (DisplayableConfigValue, Any) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = "QA Menu",
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimension.D800)
                .verticalScroll(rememberScrollState())
        ) {
            if (sessionId != null) {
                QaSection(title = "Info") {
                    QaInfoItem(
                        trailingContent = {
                            Text(text = sessionId)
                        },
                        headlineContent = {
                            Text(text = "Session ID")
                        }
                    )

                    QaInfoItem(
                        trailingContent = {
                            Text(text = languageCode)
                        },
                        headlineContent = {
                            Text(text = "Language Code")
                        }
                    )
                }
            }

            QaSection(title = "Consent") {
                QaActionItem(
                    actionText = "Reset",
                    onClick = onResetConsent,
                    enabled = true,
                    headline = {
                        Text(text = "Rest GDRP Consent")
                    }
                )

                ExperimentsList(
                    experiments = consentExperiments,
                    onExperimentOverride = onExperimentOverride
                )
            }

            if (experiments.isNotEmpty()) {
                QaSection(title = "Experiments") {
                    ExperimentsList(
                        experiments = experiments,
                        onExperimentOverride = onExperimentOverride
                    )
                }
            }

            if (configuredValues.isNotEmpty()) {
                val configGroups = configuredValues
                    .groupBy { v -> v.path.substringBefore(".") }

                val sections = configGroups.filter { g -> g.value.size > 1 }
                val leftovers = configGroups.filter { g -> g.value.size <= 1 }.values.flatten()

                QaSection(title = "Config Values") {
                    ConfigurableValuesList(
                        configuredValues = leftovers,
                        onConfigValueOverride = onConfigValueOverride
                    )
                }

                sections.forEach { (path, configuredValues) ->
                    QaSection(title = path.substringBefore(".") + " Config Values") {
                        ConfigurableValuesList(
                            configuredValues = configuredValues,
                            onConfigValueOverride = onConfigValueOverride
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun ConfigurableValuesList(
    configuredValues: List<DisplayableConfigValue>,
    onConfigValueOverride: (DisplayableConfigValue, Any) -> Unit = { _, _ -> }
) {
    Column {
        configuredValues.forEach { item ->
            when (item.value) {
                is Boolean -> {
                    val value = item.value
                    QASwitchItem(
                        checked = value,
                        onCheckedChanged = {
                            onConfigValueOverride(item, it)
                        },
                        headlineContent = { Text(text = item.name) },
                        supportingText = {
                            item.description?.let { description ->
                                Text(text = description)
                            }
                        },
                    )
                }

                else -> {
                    QaInfoItem(
                        headlineContent = { Text(text = item.name) },
                        supportingText = {
                            item.description?.let { description ->
                                Text(text = description)
                            }
                        },
                        trailingContent = {
                            Text(text = "${item.value}")
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExperimentsList(
    experiments: List<DisplayableExperiment>,
    onExperimentOverride: (DisplayableExperiment, Any) -> Unit = { _, _ -> }
) {
    Column {
        experiments.forEach { item ->
            when (item.value) {
                is Boolean -> {
                    val value = item.value
                    QASwitchItem(
                        checked = value,
                        onCheckedChanged = {
                            onExperimentOverride(item, it)
                        },
                        headlineContent = { Text(text = item.name) },
                        supportingText = {
                            item.description?.let { description ->
                                Text(text = description)
                            }
                        },
                    )
                }

                else -> {
                    QaInfoItem(
                        headlineContent = { Text(text = item.name) },
                        supportingText = {
                            item.description?.let { description ->
                                Text(text = description)
                            }
                        },
                        trailingContent = {
                            Text(text = "${item.value}")
                        },
                        isDebug = item.isDebugOnly
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewQaScreen() {
    Preview {
        QaScreen(
            configuredValues = Fakes.displayableConfigValues,
            experiments = Fakes.displayableExperiments,
            sessionId = "1234",
            consentExperiments = Fakes.consentExperiments,
            onResetConsent = {},
            languageCode = "en"
        )
    }
}