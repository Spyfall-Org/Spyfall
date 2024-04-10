package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun OfflineGameBanner(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radii.Banner.shape)
            .border(
                shape = Radii.Banner.shape,
                color = OddOneOutTheme.colors.accent.color,
                width = Dimension.D50
            )
            .padding(Dimension.D800)
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(spyfallIcon = SpyfallIcon.Info(null))
                HorizontalSpacerD600()
                Text(text = "Looks like you are offline")
            }

            VerticalSpacerD500()

            Text(
                text = "Until you are back online, pass and play mode is available.",
                typography = OddOneOutTheme.typography.Body.B600,
            )

        }
    }
}

@Preview
@Composable
private fun PreviewGameOfflineBanner() {
    Preview {
       OfflineGameBanner()
    }
}