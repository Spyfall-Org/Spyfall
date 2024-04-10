package com.dangerfield.libraries.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


/**
 * Utility for reporting app connectivity status
 */
interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}
