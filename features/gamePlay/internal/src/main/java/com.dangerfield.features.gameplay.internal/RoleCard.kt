package com.dangerfield.features.gameplay.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.text.BoldPrefixedText
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun RoleCard(
    role: String,
    isTheOddOneOut: Boolean,
    location: String?,
    text: String,
) {
    var isHidden by remember { mutableStateOf(false) }

    SubcomposeLayout(
        modifier = Modifier.padding(horizontal = Spacing.S800),
    ) { constraints ->
        val buttonPlaceable = subcompose(0) {
            Button(size = ButtonSize.Small, onClick = { isHidden = !isHidden }) {
                Text(text = if (isHidden) "Show" else "Hide")
            }
        }.first().measure(constraints)

        val contentPlaceable = subcompose(1) {
            AnimatedVisibility(
                visible = !isHidden, enter = expandVertically(), exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            OddOneOutTheme.colorScheme.surfacePrimary, radius = Radii.Round
                        )
                        .fillMaxWidth()
                        .padding(vertical = Spacing.S500, horizontal = Spacing.S1200)
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
                    Text(
                        text = text, typographyToken = OddOneOutTheme.typography.Body.B800
                    )
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
            isTheOddOneOut = true
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
            isTheOddOneOut = false
        )
    }
}

