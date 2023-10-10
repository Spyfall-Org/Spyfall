package com.dangerfield.features.forcedupdate.internal

interface AppUpdateDataSource {

    suspend fun getMinimumVersionCode(): Int?
}
