package com.dangerfield.libraries.session

import oddoneout.core.Catching

interface EnsureSessionLoaded {
    suspend operator fun invoke(): Catching<Unit>
}