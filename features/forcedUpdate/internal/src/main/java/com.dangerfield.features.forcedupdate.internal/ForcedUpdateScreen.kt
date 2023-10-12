package com.dangerfield.features.forcedupdate.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import spyfallx.coreui.components.Screen
import spyfallx.coreui.components.text.Text

@Composable
fun ForcedUpdateScreen() {
    Screen {
        Column(Modifier.padding(it)) {
            Text(text = "Forced update")
        }
    }
}