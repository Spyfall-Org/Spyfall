package com.dangerfield.spyfall.util

interface ConnectivityHelper {
    suspend fun isOnline(): Boolean
}