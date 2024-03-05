package com.dangerfield.features.inAppMessaging

import com.google.android.play.core.appupdate.AppUpdateInfo
import oddoneout.core.Try

interface GetInAppUpdateAvailability {
    suspend operator fun invoke(): Try<InAppUpdateAvailability>
}

sealed class InAppUpdateAvailability {

    class UpdateAvailable(
        val appUpdateInfo: AppUpdateInfo,
        val shouldShow: Boolean,
        val isForegroundUpdate: Boolean
    ) : InAppUpdateAvailability()

    data object UpdateInProgress : InAppUpdateAvailability()

    data object NoUpdateAvailable : InAppUpdateAvailability()

    data class UpdateReadyToInstall(val wasDownloadedInBackground: Boolean): InAppUpdateAvailability()
}