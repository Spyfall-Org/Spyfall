package com.dangerfield.spyfall.splash.forcedupdate

interface AppUpdateDataSource {

    suspend fun getMinimumVersionCode(): Int?
}
