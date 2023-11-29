package com.dangerfield.features.qa.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableConfigValue
import com.dangerfield.features.qa.internal.QaViewModel.DisplayableExperiment
import com.dangerfield.features.qa.internal.item.QASwitchItem
import com.dangerfield.features.qa.internal.item.QaInfoItem
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.Screen

@Composable
fun QaScreen(
    configuredValues: List<DisplayableConfigValue>,
    experiments: List<DisplayableExperiment>,
    modifier: Modifier = Modifier,
    onExperimentOverride: (DisplayableExperiment, Any) -> Unit = {_,_ ->},
    onNavigateBack: () -> Unit = {}
) {
    Screen(
        modifier = modifier,
        header = {
            Header(
                title = "QA Menu",
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Spacing.S800)
        ) {
            if (configuredValues.isNotEmpty()) {
                QaSection(title = "Configured Values") {
                    ConfigurableValuesList(configuredValues = configuredValues)
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
        }
    }
}

@Composable
private fun ConfigurableValuesList(configuredValues: List<DisplayableConfigValue>) {
    LazyColumn {
        items(configuredValues) { item ->
            when (item.value) {
                is Boolean -> {
                    val value = item.value as Boolean
                    QASwitchItem(
                        checked = value,
                        onCheckedChanged = {},
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
    onExperimentOverride: (DisplayableExperiment, Any) -> Unit = { _, _ ->}
) {
    LazyColumn {
        items(experiments) { item ->
            when (item.value) {
                is Boolean -> {
                    val value = item.value as Boolean
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
            experiments = Fakes.displayableExperiments
        )
    }
}