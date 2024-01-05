package com.dangerfield.features.ads.ui

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

fun Context.initializeAds() {
    MobileAds.initialize(this) {}
    MobileAds.setRequestConfiguration(
        RequestConfiguration.Builder().setTestDeviceIds(listOf("")).build()
    )
}