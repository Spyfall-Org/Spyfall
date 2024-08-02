package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.HorizontalSpacerD200
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun AsteriskText(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit
) {
    Row(modifier) {
        text()
        HorizontalSpacerD200()
        Text(
            text = "*",
            typography = OddOneOutTheme.typography.Display.D800,
            colorResource = ColorResource.CherryPop700
        )
    }
}