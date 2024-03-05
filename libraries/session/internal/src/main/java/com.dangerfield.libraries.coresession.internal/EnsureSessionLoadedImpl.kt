package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.EnsureSessionLoaded
import kotlinx.coroutines.flow.first
import oddoneout.core.Try
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class EnsureSessionLoadedImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
): EnsureSessionLoaded {
    override suspend fun invoke(): Try<Unit> = Try {
        sessionRepository.sessionFlow.first {
            it.user.id != null
        }
    }
}