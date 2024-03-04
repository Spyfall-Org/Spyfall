package com.dangerfield.features.settings.internal.referral

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReferralCode(
    val code: String,
    val redemptionStatus: RedemptionStatus
)
