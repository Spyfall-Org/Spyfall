package com.dangerfield.spyfall.legacy.util

interface ConnectivityHelper {
    suspend fun isOnline(): Boolean
}
