package com.dangerfield.werewolf

import com.dangerfield.spyfall.splash.GetGameInProgress
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import com.dangerfield.spyfall.splash.GetWerewolfGameInProgress

@Module(includes = [CoreGameModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object CoreGameModule {

    @Module
    @DisableInstallInCheck
    interface Bindings {

        @Binds
        fun bindGetGameInProgress(impl: GetWerewolfGameInProgress): GetGameInProgress
    }
}
