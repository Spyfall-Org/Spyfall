package com.dangerfield.spyfall.legacy.util

import com.dangerfield.spyfall.BuildConfig

fun isLegacyBuild() = BuildConfig.BUILD_TYPE == "legacy"
