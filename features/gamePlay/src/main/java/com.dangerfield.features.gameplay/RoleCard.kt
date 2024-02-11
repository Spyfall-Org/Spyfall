package com.dangerfield.features.gameplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.text.BoldPrefixedText
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.R
import spyfallx.ui.color.background

// TODO cleanup
// Lots of logic in this view, but it's all pretty simple.
@Composable
fun RoleCard(
    modifier: Modifier = Modifier,
    role: String,
    isTheOddOneOut: Boolean,
    isVisible: Boolean = false,
    location: String?,
    text: String?,
    onHideShowClicked: () -> Unit
) {
    RoleCardContent(
        modifier = modifier,
        onHideShowClicked = onHideShowClicked,
        isVisible = isVisible,
        role = {
            BoldPrefixedText(
                boldText = dictionaryString(R.string.roleCard_role_label),
                regularText = role,
                textAlign = TextAlign.Center,
            )
        },
        location = {
            if (location != null && !isTheOddOneOut) {
                BoldPrefixedText(
                    boldText = dictionaryString(R.string.roleCard_location_label),
                    regularText = location,
                    textAlign = TextAlign.Center,
                )
            }
        },
        isEnabled = true,
        paddingValues = PaddingValues(
            top = Spacing.S1000,
            bottom = Spacing.S1200,
            start = Spacing.S1200,
            end = Spacing.S1200,
        ),
        text = {
            if (text != null) {
                Text(
                    text = text,
                    typographyToken = OddOneOutTheme.typography.Body.B800,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Composable
private fun RoleCardContent(
    modifier: Modifier,
    isEnabled: Boolean,
    onHideShowClicked: () -> Unit,
    paddingValues: PaddingValues,
    isVisible: Boolean,
    role: @Composable (() -> Unit),
    location: @Composable (() -> Unit),
    text: @Composable (() -> Unit)
) {
    SubcomposeLayout(
        modifier = modifier.padding(horizontal = Spacing.S800),
    ) { constraints ->
        val buttonPlaceable = subcompose(0) {
            Button(size = ButtonSize.Small, onClick = {
                if (isEnabled) {
                    onHideShowClicked()
                }
            }) {
                Text(
                    text = if (!isVisible) {
                        dictionaryString(id = R.string.app_show_action)
                    } else {
                        dictionaryString(id = R.string.app_hide_action)
                    }
                )
            }
        }.first().measure(constraints)

        val contentPlaceable = subcompose(1) {
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            OddOneOutTheme.colorScheme.surfacePrimary, radius = Radii.Round
                        )
                        .fillMaxWidth()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    role()
                    location()
                    text()
                }
            }
        }.firstOrNull()?.measure(constraints)

        val width = maxOf(buttonPlaceable.width, contentPlaceable?.width ?: 0)
        val height = maxOf(
            (contentPlaceable?.height ?: 0) + buttonPlaceable.height / 2, buttonPlaceable.height
        )

        layout(width, height) {
            contentPlaceable?.place(0, 0)

            val buttonX = (width - buttonPlaceable.width) / 2
            val buttonY = height - buttonPlaceable.height

            buttonPlaceable.place(buttonX, buttonY)
        }
    }
}

@Composable
@Preview
private fun PreviewRoleCardOddOneOut() {
    PreviewContent(showBackground = true) {
        Column(Modifier.padding(vertical = 50.dp)) {
            RoleCard(
                role = "The Odd One Out!",
                text = "Don't get found out!",
                location = "The Beach",
                isTheOddOneOut = true,
                isVisible = true,
                onHideShowClicked = { -> },
            )
        }
    }
}

@Composable
@Preview
private fun PreviewRoleCardPlayer() {
    PreviewContent(showBackground = true) {
        Column(Modifier.padding(vertical = 50.dp)) {
            RoleCard(
                role = "Something that takes up space",
                text = "Fine the odd one out",
                location = "Some longer location name",
                isTheOddOneOut = false,
                isVisible = true,
                onHideShowClicked = { -> },
            )
        }
    }
}

@Composable
@Preview
private fun SmallRoleCard() {
    PreviewContent(showBackground = true) {
        Column(Modifier.padding(vertical = 50.dp)) {
            RoleCard(
                modifier = Modifier.scale(0.6f),
                role = "The Odd One Out",
                text = "Don't get found out!",
                location = null,
                isTheOddOneOut = true,
                isVisible = true,
                onHideShowClicked = { -> },
            )
        }
    }
}

