package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import com.dangerfield.features.consent.ShouldShowPrivacyOption
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.Try
import oddoneout.core.getOrElse
import oddoneout.core.logOnError
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityScoped
class ShouldShowPrivacyOptionImpl @Inject constructor(
    @ActivityContext private val context: Context,
) : ShouldShowPrivacyOption {
    override fun invoke(): Boolean = Try {
        val activity = context as Activity
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
        .logOnError()
        .getOrElse { false }

}
