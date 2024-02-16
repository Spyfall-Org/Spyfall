package com.dangerfield.features.consent

import android.app.Activity
import kotlinx.coroutines.flow.Flow
import oddoneout.core.Try

interface ConsentStatusRepository {
    fun getStatusFlow(activity: Activity,): Flow<ConsentStatus>
}

enum class ConsentStatus {
    ConsentGiven,
    ConsentDenied,
    ConsentNeeded,
    ConsentNotNeeded,
    Unknown
}