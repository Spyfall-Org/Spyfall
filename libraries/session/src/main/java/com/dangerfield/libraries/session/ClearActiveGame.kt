package com.dangerfield.libraries.session

import spyfallx.core.Try

interface ClearActiveGame {
    suspend operator fun invoke(): Try<Unit>
}