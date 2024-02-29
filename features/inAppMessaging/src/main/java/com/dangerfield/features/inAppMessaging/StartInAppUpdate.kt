package com.dangerfield.features.inAppMessaging

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.flow.Flow

interface StartInAppUpdate {
    operator fun invoke(
        appUpdateInfo: AppUpdateInfo,
        isForegroundUpdate: Boolean,
        activity: Activity,
    ): Flow<UpdateStatus>
}

interface StartInAppUpdateIfAvailable {
    operator fun invoke(
        activity: Activity,
    ): Flow<UpdateStatus>
}

sealed class UpdateStatus {
    data object NoUpdateAvailable : UpdateStatus()
    data class UpdateAvailable(
        val shouldUpdate: Boolean,
        val appUpdateInfo: AppUpdateInfo,
        val isForegroundUpdate: Boolean
    ) : UpdateStatus()

    data object WaitingUserAction : UpdateStatus()
    data object InvalidUpdateRequest : UpdateStatus()
    data object PendingDownload : UpdateStatus()
    data class Downloading(
        val bytesDownloaded: Long,
        val totalBytesToDownload: Long,
    ) : UpdateStatus()

    data class Downloaded(
        val wasBackgroundUpdate: Boolean,
    ) : UpdateStatus()

    data object Installing : UpdateStatus()
    data class UpdateNotAllowed(@AppUpdateType val type: Int) : UpdateStatus()
    data object Installed : UpdateStatus()
    data class Failed(val error: Throwable) : UpdateStatus()
    data object Canceled : UpdateStatus()
    data object Unknown : UpdateStatus()
}

/**
 * A terminal status is one that once emitted we either dont suspect or dont care about future updates
 * in a particular flow
 *
 * Note: AppUpdateInfo can only be used with startUpdateFlow() once, so we consider an event terminal
 * if we will need new info as well
 */
fun UpdateStatus.isTerminal(): Boolean {
    return when (this) {
        /**
         * If there is no update available when starting a flow,
         * we dont expect there to be any events
         *
         * Some Explanation:
         *
         * Downloaded -> Once downloaded, we show the user a message to install which just loads
         * and finishes. We can unregister the listener once downloaded occurs and svae resources
         *
         * Installed -> ideally this state wont be reached considering we unregister the listener in Downloaded
         *
         */
        is UpdateStatus.NoUpdateAvailable,
        is UpdateStatus.InvalidUpdateRequest,
        is UpdateStatus.Downloaded,
        is UpdateStatus.Installed,
        is UpdateStatus.Failed,
        is UpdateStatus.UpdateNotAllowed,
        is UpdateStatus.Canceled -> true

        is UpdateStatus.PendingDownload,
        is UpdateStatus.Downloading,
        is UpdateStatus.Installing,
        is UpdateStatus.Unknown,
        is UpdateStatus.UpdateAvailable,
        is UpdateStatus.WaitingUserAction -> false
    }
}