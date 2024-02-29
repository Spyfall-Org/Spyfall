package com.dangerfield.features.inAppMessaging.internal.update

import android.app.Activity
import androidx.datastore.core.DataStore
import com.dangerfield.features.inAppMessaging.StartInAppUpdate
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.features.inAppMessaging.isTerminal
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.storage.datastore.tryUpdateData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_NOT_AVAILABLE
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.totalBytesToDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import oddoneout.core.awaitResult
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject

@AutoBind
class StartInAppUpdateImpl @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    private val seenInAppUpdateMessages: DataStore<List<InAppUpdateMessage>>,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val clock: Clock
) : StartInAppUpdate {


    /**
     * @param appUpdateInfo the update info to start.
     * Not reusable, if update fails, a new one must be retried from [GetAppUpdateInfoImpl].
     *
     * @param isForegroundUpdate if the update should take up the foreground or not
     * @param activity the activity to start the update from
     *
     * @return a flow of [UpdateStatus] that represents the status of the update
     *
     * This function will the update flow and emit the status of the update as it progresses.
     * The update is launched in the application scope and will continue to run until the app process is destroyed.
     *
     */
    override fun invoke(
        appUpdateInfo: AppUpdateInfo,
        isForegroundUpdate: Boolean,
        activity: Activity
    ): Flow<UpdateStatus> {
        val statusChannel = Channel<UpdateStatus>(Channel.UNLIMITED)

        val versionCode = appUpdateInfo.availableVersionCode()

        val appUpdateType = if (isForegroundUpdate || appUpdateInfo.updatePriority() >= 5) {
            IMMEDIATE
        } else {
            FLEXIBLE
        }

        val updateType = appUpdateInfo.updatePriority().toUpdateType()

        val terminalStartingStatus = when {
            isForegroundUpdate && (updateType != UpdateType.MustHave) -> UpdateStatus.InvalidUpdateRequest
            !isForegroundUpdate && (updateType == UpdateType.MustHave) -> UpdateStatus.InvalidUpdateRequest
            appUpdateInfo.updateAvailability() == UPDATE_NOT_AVAILABLE -> UpdateStatus.NoUpdateAvailable
            appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> UpdateStatus.Downloaded(wasBackgroundUpdate = !isForegroundUpdate)
            appUpdateInfo.installStatus() in listOf(InstallStatus.PENDING, InstallStatus.DOWNLOADING) -> UpdateStatus.PendingDownload
            isForegroundUpdate && !appUpdateInfo.isImmediateUpdateAllowed -> UpdateStatus.UpdateNotAllowed(appUpdateType)
            !isForegroundUpdate && !appUpdateInfo.isFlexibleUpdateAllowed -> UpdateStatus.UpdateNotAllowed(appUpdateType)
            !appUpdateInfo.isUpdateTypeAllowed(appUpdateType) -> UpdateStatus.UpdateNotAllowed(appUpdateType)
            else -> null
        }

        if (terminalStartingStatus != null) { return flowOf(terminalStartingStatus) }

        val canUpdateWithoutAssetDeletion = appUpdateInfo.isUpdateTypeAllowed(
            AppUpdateOptions
                .newBuilder(appUpdateType)
                .setAllowAssetPackDeletion(false)
                .build()
        )

        val appUpdateOptions = AppUpdateOptions.newBuilder(appUpdateType)

        if (!canUpdateWithoutAssetDeletion) {
            appUpdateOptions.setAllowAssetPackDeletion(true)
        }

        val listener = object : InstallStateUpdatedListener {

            override fun onStateUpdate(installState: InstallState) {
                installState.logInfo()

                val status: UpdateStatus = installState.toUpdateStatus(isForegroundUpdate)

                statusChannel.trySend(status)

                if (status is UpdateStatus.Failed) {
                    removeUpdateFromSeenList(appUpdateInfo.availableVersionCode())
                }

                if (status.isTerminal()) {
                    appUpdateManager.unregisterListener(this)
                }
            }
        }

        appUpdateManager.registerListener(listener)

        applicationScope.launch {
            // The returned task does not complete until the user takes an action.
            val updateTask = appUpdateManager.startUpdateFlow(
                appUpdateInfo,
                activity,
                appUpdateOptions.build()
            )

            statusChannel.trySend(UpdateStatus.WaitingUserAction)

            updateTask.awaitResult()
                .onSuccess {
                    println("User action received")
                }
                .map { appUpdateToSeenList(versionCode) }
                .logOnFailure()
                .onSuccess {
                    Timber.d("Started in app update Flow for version $versionCode")
                }
        }

        return statusChannel.receiveAsFlow().distinctUntilChanged()
    }

    private suspend fun appUpdateToSeenList(versionCode: Int) = seenInAppUpdateMessages.tryUpdateData {
        it + InAppUpdateMessage(versionCode, clock.instant())
    }

    private fun removeUpdateFromSeenList(availableVersionCode: Int) {
        applicationScope.launch {
            seenInAppUpdateMessages.tryUpdateData { messages ->
                // remove the message being seen if failed, so it can be seen again.
                messages.filter { it.versionCode != availableVersionCode }
            }
        }
    }

    private fun InstallState.logInfo() {
        Timber.d(
            """
                InstallStatus: ${installStatus()}
                Bytes Downloaded: $bytesDownloaded
                Total Bytes to Download: $totalBytesToDownload
                Error Code: ${installErrorCode()}
            """.trimIndent()
        )
    }

    private fun InstallState.toUpdateStatus(
        isForegroundUpdate: Boolean
    ) = when (installStatus()) {
        InstallStatus.CANCELED -> UpdateStatus.Canceled
        InstallStatus.DOWNLOADED -> UpdateStatus.Downloaded(
            wasBackgroundUpdate = isForegroundUpdate
        )

        InstallStatus.DOWNLOADING -> UpdateStatus.Downloading(
            bytesDownloaded,
            totalBytesToDownload
        )

        InstallStatus.FAILED -> UpdateStatus.Failed(Throwable("Update failed with error code: ${installErrorCode()}"))
        InstallStatus.INSTALLED -> UpdateStatus.Installed
        InstallStatus.INSTALLING -> UpdateStatus.Installing
        InstallStatus.PENDING -> UpdateStatus.PendingDownload
        InstallStatus.REQUIRES_UI_INTENT -> UpdateStatus.InvalidUpdateRequest
        InstallStatus.UNKNOWN -> UpdateStatus.Unknown
        else -> UpdateStatus.Unknown
    }
}
