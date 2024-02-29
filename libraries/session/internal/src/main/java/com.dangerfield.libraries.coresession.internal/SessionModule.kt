package com.dangerfield.libraries.coresession.internal

import com.dangerfield.libraries.session.Session
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    fun providesSession(sessionRepository: SessionRepository): Session {
        return sessionRepository.session
    }
}