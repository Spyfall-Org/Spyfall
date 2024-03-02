package com.dangerfield.features.qa.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.HorizontalDivider
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun QaSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,

) {
    Column(modifier) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(Dimension.D200))
        Text(text = title.uppercase(), typography = OddOneOutTheme.typography.Label.L600, colorResource = OddOneOutTheme.colors.textDisabled)
        Spacer(modifier = Modifier.height(Dimension.D800))
        content()
        Spacer(modifier = Modifier.height(Dimension.D800))
    }
}