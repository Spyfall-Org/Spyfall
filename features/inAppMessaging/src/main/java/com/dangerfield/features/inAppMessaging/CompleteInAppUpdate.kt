package com.dangerfield.features.inAppMessaging

import oddoneout.core.Catching

interface CompleteInAppUpdate {
    suspend operator fun invoke(): Catching<Unit>
}