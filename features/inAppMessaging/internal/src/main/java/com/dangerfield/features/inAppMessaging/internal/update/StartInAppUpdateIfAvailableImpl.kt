package com.dangerfield.features.inAppMessaging.internal.update

import android.app.Activity
import com.dangerfield.features.inAppMessaging.GetInAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.InAppUpdateAvailability
import com.dangerfield.features.inAppMessaging.StartInAppUpdate
import com.dangerfield.features.inAppMessaging.StartInAppUpdateIfAvailable
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.collectIn
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class StartInAppUpdateIfAvailableImpl @Inject constructor(
    private val getInAppUpdateAvailability: GetInAppUpdateAvailability,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val startInAppUpdate: StartInAppUpdate,
) : StartInAppUpdateIfAvailable {

    override fun invoke(activity: Activity): Flow<UpdateStatus> = flow {
        getInAppUpdateAvailability()
            .onSuccess {
                when (it) {
                    is InAppUpdateAvailability.UpdateAvailable -> {
                        startUpdateAndStreamResult(
                            appUpdateInfo = it.appUpdateInfo,
                            isForegroundUpdate = it.isForegroundUpdate,
                            activity = activity,
                        )
                    }

                    else -> emit(UpdateStatus.NoUpdateAvailable)
                }
            }
            .onFailure {
                emit(UpdateStatus.Failed(it))
            }
    }

    private fun FlowCollector<UpdateStatus>.startUpdateAndStreamResult(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo,
        isForegroundUpdate: Boolean,
    ) {
        startInAppUpdate(
            appUpdateInfo = appUpdateInfo,
            isForegroundUpdate = isForegroundUpdate,
            activity = activity,
        ).collectIn(applicationScope) { status ->
            emit(status)
        }
    }
}