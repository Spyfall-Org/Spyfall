package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.splash.CheckForRequiredSpyfallUpdate
import com.dangerfield.spyfall.splash.CheckForRequiredUpdate
import com.dangerfield.spyfall.splash.GetGameInProgress
import com.dangerfield.spyfall.splash.GetSpyfallGameInProgress
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

        @Binds
        fun bindCheckForRequiredUpdate(impl: CheckForRequiredSpyfallUpdate): CheckForRequiredUpdate
    }
}
