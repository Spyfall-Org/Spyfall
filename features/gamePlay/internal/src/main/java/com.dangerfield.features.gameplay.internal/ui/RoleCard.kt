package com.dangerfield.features.gameplay.internal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.text.BoldPrefixedText
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

// TODO cleanup
// Lots of logic in this view, but it's all pretty simple.
@Composable
fun RoleCard(
    role: String,
    isTheOddOneOut: Boolean,
    isVisible: Boolean = false,
    location: String?,
    text: String?,
    onHideShowClicked: () -> Unit
) {
    SubcomposeLayout(
        modifier = Modifier.padding(horizontal = Spacing.S800),
    ) { constraints ->
        val buttonPlaceable = subcompose(0) {
            Button(size = ButtonSize.Small, onClick = onHideShowClicked) {
                Text(text = if (!isVisible) "Show" else "Hide")
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
                        .padding(vertical = Spacing.S1000, horizontal = Spacing.S1200)
                        .padding(bottom = Spacing.S500),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoldPrefixedText(
                        boldText = "Role: ",
                        regularText = role,
                        textAlign = TextAlign.Center,
                    )

                    if (location != null && !isTheOddOneOut) {
                        BoldPrefixedText(
                            boldText = "Location: ",
                            regularText = location,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (text != null) {
                        Text(
                            text = text,
                            typographyToken = OddOneOutTheme.typography.Body.B800,
                            textAlign = TextAlign.Center
                        )
                    }
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

@Composable
@Preview
private fun PreviewRoleCardPlayer() {
    PreviewContent(showBackground = true) {
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

