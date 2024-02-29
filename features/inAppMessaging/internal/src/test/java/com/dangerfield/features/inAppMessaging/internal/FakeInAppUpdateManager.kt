package com.dangerfield.features.inAppMessaging.internal

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_NOT_AVAILABLE
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.totalBytesToDownload
import io.mockk.every
import io.mockk.mockk
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class FakeInAppUpdateManagerRule(private val fakeInAppUpdateManager: FakeInAppUpdateManager) :
    TestRule {
    override fun apply(base: Statement, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                fakeInAppUpdateManager.reset()
                base.evaluate()
            }
        }
}

class FakeInAppUpdateManager : AppUpdateManager {

    private var mockAppUpdateInfo: AppUpdateInfo? = null
    private var listeners = mutableListOf<InstallStateUpdatedListener>()
    private var shouldCompleteSucceed = false
    private var onUserAction: ((UserAction) -> Unit)? = null
    private var isShowingFlexibleUpdateMessage: Boolean = false
    private var isShowingImmediateUpdateMessage: Boolean = false

    val numberOfListeners: Int get() = listeners.size

    enum class UserAction {
        AcceptUpdate,
        RejectUpdate,
        CancelUpdate,
    }

    override fun completeUpdate(): Task<Void> {
        return if (shouldCompleteSucceed) {
            Tasks.forResult(null)
        } else {
            Tasks.forException(IllegalStateException("Complete update failed"))
        }
    }

    override fun getAppUpdateInfo(): Task<AppUpdateInfo> {
        checkAppInfoInitialized()
        return Tasks.forResult(mockAppUpdateInfo)
    }

    override fun registerListener(listener: InstallStateUpdatedListener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: InstallStateUpdatedListener) {
        listeners.remove(listener)
    }

    /**
     * @returns a Task that completes once the dialog has been accepted, denied or closed
     */
    override fun startUpdateFlow(
        appUpdateInfo: AppUpdateInfo,
        p1: Activity,
        appUpdateOptions: AppUpdateOptions
    ): Task<Int> {
        val taskCompletionSource = TaskCompletionSource<Int>()
        handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        ) { userAction ->
            when (userAction) {
                UserAction.AcceptUpdate -> taskCompletionSource.setResult(Activity.RESULT_OK)
                UserAction.RejectUpdate -> taskCompletionSource.setResult(Activity.RESULT_CANCELED)
                UserAction.CancelUpdate -> taskCompletionSource.setResult(Activity.RESULT_CANCELED)
            }
        }

        return taskCompletionSource.task
    }

    override fun startUpdateFlowForResult(
        appUpdateInfo: AppUpdateInfo,
        p1: ActivityResultLauncher<IntentSenderRequest>,
        appUpdateOptions: AppUpdateOptions
    ): Boolean {

        return handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        )
    }

    override fun startUpdateFlowForResult(
        appUpdateInfo: AppUpdateInfo,
        @AppUpdateType updateType: Int,
        p2: Activity,
        p3: Int
    ): Boolean {

        val appUpdateOptions = AppUpdateOptions
            .newBuilder(updateType)
            .build()

        return handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        )
    }

    override fun startUpdateFlowForResult(
        appUpdateInfo: AppUpdateInfo,
        @AppUpdateType appUpdateType: Int,
        p2: IntentSenderForResultStarter,
        p3: Int
    ): Boolean {

        val appUpdateOptions = AppUpdateOptions
            .newBuilder(appUpdateType)
            .build()

        return handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        )
    }

    override fun startUpdateFlowForResult(
        appUpdateInfo: AppUpdateInfo,
        p1: Activity,
        appUpdateOptions: AppUpdateOptions,
        p3: Int
    ): Boolean {
        return handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        )
    }

    override fun startUpdateFlowForResult(
        appUpdateInfo: AppUpdateInfo,
        p1: IntentSenderForResultStarter,
        appUpdateOptions: AppUpdateOptions,
        p3: Int
    ): Boolean {
        return handleFlowStart(
            appUpdateInfo = appUpdateInfo,
            appUpdateOptions = appUpdateOptions
        )
    }

    private fun handleFlowStart(
        appUpdateInfo: AppUpdateInfo,
        appUpdateOptions: AppUpdateOptions,
        onAction: (UserAction) -> Unit = {},
    ): Boolean {
        checkAppInfoInitialized()

        if (
            appUpdateInfo.updateAvailability() == UPDATE_NOT_AVAILABLE
            || !appUpdateInfo.isUpdateTypeAllowed(appUpdateOptions)
        ) {
            return false
        }

        if (appUpdateInfo.isImmediateUpdateAllowed && appUpdateInfo.updatePriority() >= 5) {
            isShowingImmediateUpdateMessage = true
        } else {
            isShowingFlexibleUpdateMessage = true
        }

        onUserAction = { userAction ->
            onAction(userAction)
            when (userAction) {
                UserAction.AcceptUpdate -> {
                    updateState(
                        installStatus = InstallStatus.PENDING,
                        bytesDownloaded = 0,
                        totalBytesToDownload = appUpdateInfo.totalBytesToDownload(),
                        errorCode = InstallErrorCode.NO_ERROR
                    )
                }

                UserAction.RejectUpdate,
                UserAction.CancelUpdate -> {
                    updateState(
                        installStatus = InstallStatus.CANCELED,
                        bytesDownloaded = 0,
                        totalBytesToDownload = appUpdateInfo.totalBytesToDownload(),
                        errorCode = InstallErrorCode.NO_ERROR
                    )
                }
            }
        }
        return true
    }

    fun completeFails() {
        shouldCompleteSucceed = false
    }

    fun completeSucceeds() {
        shouldCompleteSucceed = true
    }

    fun userAcceptsUpdate() {
        if (!isShowingImmediateUpdateMessage && !isShowingFlexibleUpdateMessage) {
            throw IllegalStateException("You must start the update flow before accepting the update")
        }
        isShowingFlexibleUpdateMessage = false
        isShowingImmediateUpdateMessage = false
        onUserAction?.invoke(UserAction.AcceptUpdate)
    }

    fun userRejectsUpdate() {
        if (!isShowingImmediateUpdateMessage && !isShowingFlexibleUpdateMessage) {
            throw IllegalStateException("You must start the update flow before accepting the update")
        }
        isShowingFlexibleUpdateMessage = false
        isShowingImmediateUpdateMessage = false
        onUserAction?.invoke(UserAction.AcceptUpdate)
    }

    fun fakeAppUpdateInfo() = mockAppUpdateInfo

    /**
     * Used for the initial setting of update details
     * NOT used for updating download or install state
     *
     * for that please use [updateState]
     *
     */
    fun setAppUpdateInfo(
        updatePriority: Int = 0,
        versionCode: Int = 0,
        @InstallStatus installStatus: Int = InstallStatus.UNKNOWN,
        updateAvailable: Boolean,
        totalBytes: Long = 5,
        bytesDownloaded: Long = 0,
        daysSinceRelease: Int = 0,
        requireAssetDeletion: Boolean = false,
        packageName: String = "com.example",
        @InstallStatus allowedUpdateTypes: List<Int> = listOf(IMMEDIATE, FLEXIBLE),
    ) {
        mockAppUpdateInfo = getAppUpdateInfoMock(
            updatePriority = updatePriority,
            versionCode = versionCode,
            installStatus = installStatus,
            updateAvailable = updateAvailable,
            totalBytes = totalBytes,
            bytesDownloaded = bytesDownloaded,
            daysSinceRelease = daysSinceRelease,
            requireAssetDeletion = requireAssetDeletion,
            packageName = packageName,
            allowedUpdateTypes = allowedUpdateTypes
        )
    }

    fun reset() {
        mockAppUpdateInfo = null
        onUserAction = null
        isShowingFlexibleUpdateMessage = false
        isShowingImmediateUpdateMessage = false
        shouldCompleteSucceed = false
        listeners.clear()
    }

    fun updateFails() {
        checkAppInfoInitialized()
        updateState(
            installStatus = InstallStatus.FAILED,
            bytesDownloaded = mockAppUpdateInfo!!.bytesDownloaded(),
            totalBytesToDownload = mockAppUpdateInfo!!.totalBytesToDownload(),
            errorCode = 1049
        )
    }


    fun updateCancelled() {
        checkAppInfoInitialized()
        updateState(
            installStatus = InstallStatus.CANCELED,
            bytesDownloaded = mockAppUpdateInfo!!.bytesDownloaded(),
            totalBytesToDownload = mockAppUpdateInfo!!.totalBytesToDownload(),
            errorCode = 1049
        )
    }

    fun updateStarts() {
        checkAppInfoInitialized()

        updateState(
            installStatus = InstallStatus.DOWNLOADING,
            bytesDownloaded = 0,
            totalBytesToDownload = mockAppUpdateInfo!!.totalBytesToDownload()
        )
    }

    fun updateProgresses(bytesDownloaded: Long) {
        checkAppInfoInitialized()

        if (bytesDownloaded >= mockAppUpdateInfo!!.totalBytesToDownload()) {
            updateState(
                installStatus = InstallStatus.DOWNLOADED,
                bytesDownloaded = bytesDownloaded,
                totalBytesToDownload = mockAppUpdateInfo!!.totalBytesToDownload()
            )
        } else {
            updateState(
                installStatus = InstallStatus.DOWNLOADING,
                bytesDownloaded = bytesDownloaded,
                totalBytesToDownload = mockAppUpdateInfo!!.totalBytesToDownload()
            )
        }
    }

    fun updateState(
        @InstallStatus installStatus: Int,
        bytesDownloaded: Long? = null,
        totalBytesToDownload: Long? = null,
        @InstallErrorCode errorCode: Int = InstallErrorCode.NO_ERROR
    ) {

        checkAppInfoInitialized()

        every { mockAppUpdateInfo!!.installStatus() } returns installStatus
        bytesDownloaded?.let { every { mockAppUpdateInfo!!.bytesDownloaded() } returns it }
        totalBytesToDownload?.let { every { mockAppUpdateInfo!!.totalBytesToDownload() } returns it }

        val installState = InstallState.zza(
            installStatus,
            mockAppUpdateInfo!!.bytesDownloaded(),
            mockAppUpdateInfo!!.totalBytesToDownload,
            errorCode,
            mockAppUpdateInfo!!.packageName()
        )

        val listenersSnapshot = listeners.toList()
        listenersSnapshot.forEach { listener ->
            listener.onStateUpdate(installState)
        }
    }

    fun isShowingFlexibleUpdateMessage() = isShowingFlexibleUpdateMessage

    fun isShowingImmediateUpdateMessage() = isShowingImmediateUpdateMessage

    private fun checkAppInfoInitialized() {
        if (mockAppUpdateInfo == null) {
            throw IllegalStateException("You must set the update info before interacting with the manager")
        }
    }

    private fun getAppUpdateInfoMock(
        updatePriority: Int = 0,
        versionCode: Int = 0,
        @InstallStatus installStatus: Int = InstallStatus.UNKNOWN,
        updateAvailable: Boolean,
        totalBytes: Long = 5,
        bytesDownloaded: Long = 0,
        daysSinceRelease: Int = 0,
        requireAssetDeletion: Boolean = false,
        packageName: String = "com.example",
        @AppUpdateType allowedUpdateTypes: List<Int>
    ): AppUpdateInfo {
        val mockAppUpdateInfo = mockk<AppUpdateInfo>()
        every { mockAppUpdateInfo.updatePriority() } returns updatePriority
        every { mockAppUpdateInfo.availableVersionCode() } returns versionCode
        every { mockAppUpdateInfo.installStatus() } returns installStatus
        every { mockAppUpdateInfo.updateAvailability() } returns if (updateAvailable) UPDATE_AVAILABLE else UPDATE_NOT_AVAILABLE
        every { mockAppUpdateInfo.totalBytesToDownload() } returns totalBytes
        every { mockAppUpdateInfo.bytesDownloaded() } returns bytesDownloaded
        every { mockAppUpdateInfo.clientVersionStalenessDays() } returns daysSinceRelease
        every { mockAppUpdateInfo.packageName() } returns packageName

        // allowed update type is both by default
        every {
            mockAppUpdateInfo.isUpdateTypeAllowed(any<Int>())
        } answers {
            val updateType = it.invocation.args[0] as Int
            updateType in allowedUpdateTypes
        }


        every {
            mockAppUpdateInfo.isUpdateTypeAllowed(any<AppUpdateOptions>())
        } answers {
            val options = it.invocation.args[0] as AppUpdateOptions
            val isTypeAllowed = options.appUpdateType() in allowedUpdateTypes

            val isTryingToNotDeleteAssets = !options.allowAssetPackDeletion()

            val isDeletionTypeAllowed = if (isTryingToNotDeleteAssets) {
                !requireAssetDeletion
            } else {
                true // asset deletion is always allowed. We just try to do it without deleting at first.
            }

            isTypeAllowed && isDeletionTypeAllowed
        }

        return mockAppUpdateInfo
    }
}
