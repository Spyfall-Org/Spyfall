package com.dangerfield.features.qa.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.components.Divider
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun QaSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,

) {
    Column(modifier) {
        Divider()
        Spacer(modifier = Modifier.height(Spacing.S800))
        Text(text = title.uppercase(), typographyToken = SpyfallTheme.typography.Label.L600, color = SpyfallTheme.colorScheme.textDisabled)
        Spacer(modifier = Modifier.height(Spacing.S800))
        content()
        Spacer(modifier = Modifier.height(Spacing.S800))
    }
}