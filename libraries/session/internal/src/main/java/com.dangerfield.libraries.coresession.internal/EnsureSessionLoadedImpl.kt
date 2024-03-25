package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.EnsureSessionLoaded
import kotlinx.coroutines.flow.first
import oddoneout.core.Catching
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class EnsureSessionLoadedImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
): EnsureSessionLoaded {
    override suspend fun invoke(): Catching<Unit> = Catching {
        sessionRepository.sessionFlow.first {
            it.user.id != null
        }
    }
}