package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.HorizontalSpacerS200
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun AsteriskText(text: @Composable () -> Unit) {
    Row {
        text()
        HorizontalSpacerS200()
        Text(
            text = "*",
            typographyToken = SpyfallTheme.typography.Display.D800,
            colorPrimitive = ColorPrimitive.CherryPop700
        )
    }
}