package com.dangerfield.libraries.session

import oddoneout.core.Catching

interface UpdateActiveGame {
    suspend operator fun invoke(activeGame: ActiveGame): Catching<Unit>
}