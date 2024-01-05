package com.dangerfield.features.qa.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableConfigValue
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableExperiment
import com.dangerfield.features.qa.internal.item.QASwitchItem
import com.dangerfield.features.qa.internal.item.QaInfoItem
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text

// TODO add support for devs to manually add their own QA items
@Composable
fun QaScreen(
    configuredValues: List<DisplayableConfigValue>,
    experiments: List<DisplayableExperiment>,
    modifier: Modifier = Modifier,
    sessionId: String? = null,
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
        ScrollingColumnWithFadingEdge(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Spacing.S800)
        ) {
            if (sessionId != null) {
                QaSection(title = "Info") {
                    QaInfoItem {
                        Text(text = "Session ID: $sessionId")
                    }
                }
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
                QaSection(title = "Configured Values") {
                    ConfigurableValuesList(
                        configuredValues = configuredValues,
                        onConfigValueOverride = onConfigValueOverride
                    )
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
                        isDebug = item.isDebugOnly
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
@ThemePreviews
fun PreviewQaScreen() {
    PreviewContent {
        QaScreen(
            configuredValues = Fakes.displayableConfigValues,
            experiments = Fakes.displayableExperiments,
            sessionId = "1234"
        )
    }
}