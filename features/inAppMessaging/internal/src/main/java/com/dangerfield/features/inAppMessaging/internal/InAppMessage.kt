package com.dangerfield.features.inAppMessaging.internal

import com.dangerfield.features.inAppMessaging.InAppMessagePriority
import com.dangerfield.libraries.navigation.Router
import kotlin.time.Duration

data class InAppMessage(
    val spamSpacer: Duration,
    val openMessage: (Router) -> Unit,
    val cancelIfInterrupted: Boolean,
    val priority: InAppMessagePriority,
    val tag: String
) : Comparable<InAppMessage> {
    override fun compareTo(other: InAppMessage): Int = priority.compareTo(other.priority)
}