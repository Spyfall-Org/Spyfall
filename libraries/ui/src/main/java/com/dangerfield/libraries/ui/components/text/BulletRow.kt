package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerD800
import com.dangerfield.oddoneoout.libraries.dictionary.R

@Composable
fun BulletRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    Row(modifier = modifier) {
        Text(text = dictionaryString(id = R.string.app_bullet_point))
        HorizontalSpacerD800()
        content()
    }
}