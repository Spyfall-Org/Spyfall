package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.ClearActiveGame
import oddoneout.core.Catching
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class ClearActiveGameImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
): ClearActiveGame {
    override suspend operator fun invoke(): Catching<Unit> = Catching {
        sessionRepository.updateActiveGame(null)
    }
}