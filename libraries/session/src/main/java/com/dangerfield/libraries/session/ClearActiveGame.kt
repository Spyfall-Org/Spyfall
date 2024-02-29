package com.dangerfield.libraries.session

import oddoneout.core.Try

interface ClearActiveGame {
    suspend operator fun invoke(): Try<Unit>
}