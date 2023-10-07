package com.dangerfield.spyfall.legacy.ui.forcedupdate

interface AppUpdateDataSource {

    suspend fun getMinimumVersionCode(): Int?
}
