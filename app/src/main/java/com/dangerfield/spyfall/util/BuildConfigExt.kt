package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.BuildConfig

fun isFreeFlavor() = BuildConfig.FLAVOR == "free"
fun isPaidFlavor() = BuildConfig.FLAVOR == "paid"
fun isLegacyBuild() = BuildConfig.BUILD_TYPE == "legacy"
