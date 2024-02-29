package com.dangerfield.features.inAppMessaging.internal.update

import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class InAppUpdateMessage(
    val versionCode: Int,
    val shown: Instant
)