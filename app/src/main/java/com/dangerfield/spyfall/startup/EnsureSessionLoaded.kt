package com.dangerfield.spyfall.startup

import com.dangerfield.libraries.session.SessionRepository
import kotlinx.coroutines.flow.first
import spyfallx.core.Try
import spyfallx.core.throwIfDebug
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureSessionLoaded @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Try<Unit> =
        Try {
            sessionRepository.sessionFlow.first()
        }
            .throwIfDebug()
            .ignoreValue()
}