package com.dangerfield.features.inAppMessaging.internal.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.text.ProvideTextConfig
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun ProgressRow(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .background(OddOneOutTheme.colors.surfaceDisabled.color),
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(OddOneOutTheme.colors.accent.color)
        ) {}

        Box(
            modifier = Modifier
        ) {
            ProvideTextConfig(
                typographyToken = OddOneOutTheme.typography.Body.B600,
                color = OddOneOutTheme.colors.onAccent
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun ProgressRowPreview() {
    Preview(
    ) {
        ProgressRow(progress = 0.5f) {
            Text("Hello")
        }
    }
}