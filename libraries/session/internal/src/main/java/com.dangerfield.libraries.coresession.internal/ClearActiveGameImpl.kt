package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.ClearActiveGame
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import javax.inject.Inject

@AutoBind
class ClearActiveGameImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
): ClearActiveGame {
    override suspend operator fun invoke(): Try<Unit> = Try {
        sessionRepository.updateActiveGame(null)
    }
}