package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.HorizontalSpacerD200
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun AsteriskText(text: @Composable () -> Unit) {
    Row {
        text()
        HorizontalSpacerD200()
        Text(
            text = "*",
            typographyToken = OddOneOutTheme.typography.Display.D800,
            colorResource = ColorResource.CherryPop700
        )
    }
}