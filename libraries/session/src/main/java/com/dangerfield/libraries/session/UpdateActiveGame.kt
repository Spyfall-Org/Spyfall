package com.dangerfield.libraries.session

import spyfallx.core.Try

interface UpdateActiveGame {
    suspend operator fun invoke(activeGame: ActiveGame): Try<Unit>
}