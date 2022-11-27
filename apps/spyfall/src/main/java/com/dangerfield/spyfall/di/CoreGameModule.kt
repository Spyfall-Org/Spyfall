package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.splash.GetSpyfallGameInProgress
import com.dangerfield.spyfall.splash.GetGameInProgress
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck


@Module(includes = [CoreGameModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object CoreGameModule {

    @Module
    @DisableInstallInCheck
    interface Bindings {

        @Binds
        fun bindGetGameInProgress(impl: GetSpyfallGameInProgress): GetGameInProgress
    }
}
