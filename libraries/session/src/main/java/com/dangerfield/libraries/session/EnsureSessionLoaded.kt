package com.dangerfield.libraries.session

import spyfallx.core.Try

interface EnsureSessionLoaded {
    suspend operator fun invoke(): Try<Unit>
}