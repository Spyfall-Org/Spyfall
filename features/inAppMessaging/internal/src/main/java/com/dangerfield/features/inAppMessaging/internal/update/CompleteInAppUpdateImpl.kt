package com.dangerfield.features.inAppMessaging.internal.update

import com.dangerfield.features.inAppMessaging.CompleteInAppUpdate
import com.google.android.play.core.appupdate.AppUpdateManager
import oddoneout.core.Try
import oddoneout.core.awaitResult
import oddoneout.core.ignoreValue
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class CompleteInAppUpdateImpl @Inject constructor(
    private val appUpdateManager: AppUpdateManager
) : CompleteInAppUpdate {
    override suspend fun invoke(): Try<Unit> {
        return appUpdateManager
            .completeUpdate()
            .awaitResult()
            .logOnFailure()
            .ignoreValue()
    }
}