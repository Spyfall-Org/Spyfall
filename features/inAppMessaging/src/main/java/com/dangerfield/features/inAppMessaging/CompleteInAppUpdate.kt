package com.dangerfield.features.inAppMessaging

import oddoneout.core.Try

interface CompleteInAppUpdate {
    suspend operator fun invoke(): Try<Unit>
}