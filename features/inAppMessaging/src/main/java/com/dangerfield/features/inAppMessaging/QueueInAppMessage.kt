package com.dangerfield.features.inAppMessaging

import com.dangerfield.features.inAppMessaging.InAppMessagePriority.NonCritical
import com.dangerfield.libraries.navigation.Router
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface QueueInAppMessage {
    operator fun invoke(
        spamSpacer: Duration = 15.seconds,
        cancelIfInterrupted: Boolean = false,
        priority: InAppMessagePriority = NonCritical,
        tag: String,
        openMessage: (Router) -> Unit,
    )
}

enum class InAppMessagePriority {
    Critical, NonCritical
}