package com.dangerfield.features.consent.internal

import android.app.Activity
import android.content.Context
import com.dangerfield.features.consent.OpenConsentForm
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import oddoneout.core.Try
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

@AutoBind
@ActivityScoped
class OpenConsentFormImpl @Inject constructor(
    @ActivityContext private val context: Context,
    private val gdrpConsentManager: GDRPConsentManager,
) : OpenConsentForm {

    override suspend fun invoke() {
        Try {
            Timber.d("About to open GDRP consent form manually")
            val activity = context as Activity
            gdrpConsentManager.showConsentForm(activity)
        }
            .throwIfDebug()
            .logOnError()
    }
}