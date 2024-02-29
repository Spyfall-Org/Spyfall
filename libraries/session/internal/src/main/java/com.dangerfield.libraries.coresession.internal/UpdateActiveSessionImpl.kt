package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.UpdateActiveGame
import oddoneout.core.Try
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UpdateActiveSessionImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
) : UpdateActiveGame {
    override suspend operator fun invoke(activeGame: ActiveGame): Try<Unit> = Try {
        sessionRepository.updateActiveGame(activeGame)
    }
}