package com.dangerfield.oddoneout.legacy.util

interface ConnectivityHelper {
    suspend fun isOnline(): Boolean
}
