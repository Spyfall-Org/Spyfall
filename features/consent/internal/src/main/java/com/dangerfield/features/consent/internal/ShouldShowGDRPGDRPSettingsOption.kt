package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import com.dangerfield.features.consent.ShouldShowGDRPSettingsOption
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityScoped
class ShouldShowGDRPGDRPSettingsOption @Inject constructor(
    @ActivityContext private val context: Context,
    private val gdrpConsentManager: GDRPConsentManager
) : ShouldShowGDRPSettingsOption {
    override fun invoke(): Boolean = Catching {
        gdrpConsentManager.shouldShowSettingsOption(context as Activity)
    }
        .logOnFailure()
        .getOrElse { false }
}
