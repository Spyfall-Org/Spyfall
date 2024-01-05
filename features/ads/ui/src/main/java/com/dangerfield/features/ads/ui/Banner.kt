package com.dangerfield.features.ads.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dangerfield.features.ads.LocalAdsConfig
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.pxToDp
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import spyfallx.ui.color.background

@SuppressLint("MissingPermission")
@Composable
fun AdBanner(
    ad: OddOneOutAd,
    modifier: Modifier = Modifier
) {

    if (LocalInspectionMode.current) {
        PreviewAd()
    } else if (LocalAdsConfig.current.isAdEnabled(ad)) {

        Box {
            // used to take up the the space for an add while it loads so the ui doesnt jump around
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(OddOneOutTheme.colorScheme.surfacePrimary)
            )
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = context.getString(ad.resId)
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}

@Composable
private fun PreviewAd() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(OddOneOutTheme.colorScheme.textWarning),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Preview Ad",
            typographyToken = OddOneOutTheme.typography.Body.B700.Bold,
            color = OddOneOutTheme.colorScheme.text,
        )
    }
}