package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.BuildConfig

fun isLegacyBuild() = BuildConfig.BUILD_TYPE == "legacy"
