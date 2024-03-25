package com.dangerfield.features.inAppMessaging.internal.update

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import oddoneout.core.Catching
import oddoneout.core.awaitCatching
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

interface GetAppUpdateInfo {
    suspend operator fun invoke(): Catching<AppUpdateInfo>
}

@AutoBind
class GetAppUpdateInfoImpl @Inject constructor(
    private val appUpdateManager: AppUpdateManager
) : GetAppUpdateInfo {

    override suspend operator fun invoke(): Catching<AppUpdateInfo> =
        appUpdateManager.appUpdateInfo.awaitCatching()
}