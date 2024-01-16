package com.dangerfield.libraries.session

import oddoneout.core.Try

interface EnsureSessionLoaded {
    suspend operator fun invoke(): Try<Unit>
}