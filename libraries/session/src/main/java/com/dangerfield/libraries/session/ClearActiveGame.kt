package com.dangerfield.libraries.session

import oddoneout.core.Catching

interface ClearActiveGame {
    suspend operator fun invoke(): Catching<Unit>
}