package com.dangerfield.features.inAppMessaging.internal.update

import androidx.datastore.core.DataStore
import com.dangerfield.features.inAppMessaging.GetInAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability.UpdateInProgress
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability.UpdateReadyToInstall
import com.google.android.play.core.install.model.InstallStatus.DOWNLOADED
import com.google.android.play.core.install.model.InstallStatus.DOWNLOADING
import com.google.android.play.core.install.model.InstallStatus.PENDING
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.installStatus
import kotlinx.coroutines.flow.firstOrNull
import oddoneout.core.BuildInfo
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.time.Clock
import java.time.Duration.ofDays
import javax.inject.Inject

@AutoBind
class GetInAppUpdateAvailabilityImpl @Inject constructor(
    private val getAppUpdateInfo: GetAppUpdateInfo,
    private val seenInAppUpdateMessages: DataStore<List<InAppUpdateMessage>>,
    private val clock: Clock,
    private val daysBetweenInAppUpdateMessages: DaysBetweenInAppUpdateMessages,
    private val buildInfo: BuildInfo
) : GetInAppUpdateAvailability {

    override suspend fun invoke(): Catching<InAppUpdateAvailability> {
        return getAppUpdateInfo()
            .map { updateInfo ->
                val isUpdateAvailable = updateInfo.updateAvailability() == UPDATE_AVAILABLE
                val updateType = updateInfo.updatePriority().toUpdateType()
                val isBackgroundUpdate = updateType != UpdateType.MustHave

                val terminalStartingStatus = when  {
                    updateInfo.installStatus == DOWNLOADED -> UpdateReadyToInstall(isBackgroundUpdate)
                    updateInfo.installStatus in listOf(PENDING, DOWNLOADING) -> UpdateInProgress
                    !isUpdateAvailable -> InAppUpdateAvailability.NoUpdateAvailable
                    else -> null
                }

                if (terminalStartingStatus != null) {
                    return@map terminalStartingStatus
                }

                val seenInAppUpdateMessages = seenInAppUpdateMessages.data.firstOrNull().orEmpty()

                val lastTimeThisMessageWasSeen = seenInAppUpdateMessages.find { message ->
                    message.versionCode == updateInfo.availableVersionCode()
                }?.shown

                val xDaysAgo = clock
                    .instant()
                    .minus(ofDays(daysBetweenInAppUpdateMessages().toLong()))

                val isTestable = buildInfo.isDebug || buildInfo.isQA

                val shouldShow = when {
                    updateType == UpdateType.Ignorable && !isTestable -> false
                    lastTimeThisMessageWasSeen == null -> true
                    lastTimeThisMessageWasSeen.isBefore(xDaysAgo) -> true
                    else -> false
                }

                Timber.d(
                    """
                        InAppUpdateState: ${updateInfo.installStatus}
                        isUpdateAvailable: true
                        updateType: $updateType
                        lastTimeThisMessageWasSeen: $lastTimeThisMessageWasSeen
                        shouldShow: $shouldShow
                    """.trimIndent()
                )
                InAppUpdateAvailability.UpdateAvailable(
                    appUpdateInfo = updateInfo,
                    shouldShow = shouldShow,
                    isForegroundUpdate = !isBackgroundUpdate
                )

            }
            .onSuccess {
                Timber.d(
                    "InAppUpdateAvailability: ${
                        when (it) {
                            is InAppUpdateAvailability.UpdateAvailable -> "UpdateAvailable"
                            is InAppUpdateAvailability.NoUpdateAvailable -> "NoUpdateAvailable"
                            is UpdateReadyToInstall -> "UpdateReadyToInstall"
                            UpdateInProgress -> "UpdateInProgress"
                        }
                    }"
                )
            }
            .logOnFailure()
    }
}

fun Int.toUpdateType(): UpdateType {
    return when (this) {
        0 -> UpdateType.Ignorable
        1 -> UpdateType.GoodToHave
        2 -> UpdateType.ShouldHave
        5 -> UpdateType.MustHave
        else -> UpdateType.ShouldHave
    }
}

enum class UpdateType {
    Ignorable,
    GoodToHave,
    ShouldHave,
    MustHave
}