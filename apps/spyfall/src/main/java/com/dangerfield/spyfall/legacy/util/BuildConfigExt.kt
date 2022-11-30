package com.dangerfield.spyfall.legacy.util

import com.dangerfield.spyfall.BuildConfig

fun isLegacyBuild() = BuildConfig.FLAVOR.lowercase().contains("legacy")
