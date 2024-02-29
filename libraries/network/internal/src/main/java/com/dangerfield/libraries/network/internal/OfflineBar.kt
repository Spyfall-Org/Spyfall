package com.dangerfield.libraries.network.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.libraries.network.internal.R

@Composable
fun OfflineBar(isOffline: Boolean) {
    AnimatedVisibility(
        visible = isOffline,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OddOneOutTheme.colors.textWarning.color),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dictionaryString(R.string.odd_one_out_is_offline),
                typographyToken = OddOneOutTheme.typography.Body.B700,
                colorResource = OddOneOutTheme.colors.text,
            )
        }
    }
}
