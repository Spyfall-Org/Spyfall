package com.dangerfield.features.gameplay.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.Spacing
import spyfallx.coreui.color.background
import spyfallx.coreui.components.button.Button
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.theme.SpyfallTheme

@Composable
fun RoleCard(
    role: String,
    text: String,
) {
    var isHidden by remember { mutableStateOf(false) }

    SubcomposeLayout { constraints ->
        val buttonPlaceable = subcompose(0) {
            Button(onClick = { isHidden = !isHidden }) {
                Text(text = if (isHidden) "Show" else "Hide")
            }
        }.first().measure(constraints)


        val contentPlaceable = subcompose(1) {
            AnimatedVisibility(
                visible = !isHidden,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .background(SpyfallTheme.colorScheme.surfacePrimary, radius = Radii.Round)
                        .padding(vertical = Spacing.S1000, horizontal = Spacing.S1200)
                        .padding(bottom = Spacing.S500),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Role: $role",
                        typographyToken = SpyfallTheme.typography.Default.Bold
                    )
                    Text(text = text)
                }
            }
        }.firstOrNull()?.measure(constraints)

        val width = maxOf(buttonPlaceable.width, contentPlaceable?.width ?: 0)
        val height = maxOf((contentPlaceable?.height ?: 0) + buttonPlaceable.height / 2, buttonPlaceable.height)

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
private fun PreviewRoleCard() {
    PreviewContent(showBackground = true) {
        RoleCard(
            role = "The Spy!",
            text = "Don't get found out!"
        )
    }
}