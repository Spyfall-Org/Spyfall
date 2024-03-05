package com.dangerfield.libraries.coresession.internal

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionData(
    val id: Long,
    val startedAt: Long
)