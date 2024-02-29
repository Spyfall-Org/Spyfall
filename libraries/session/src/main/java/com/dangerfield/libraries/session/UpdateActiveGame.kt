package com.dangerfield.libraries.session

import oddoneout.core.Try

interface UpdateActiveGame {
    suspend operator fun invoke(activeGame: ActiveGame): Try<Unit>
}