package com.dangerfield.werewolf

import com.dangerfield.spyfall.splash.CheckForRequiredUpdate
import com.dangerfield.spyfall.splash.CheckForRequiredWerewolfUpdate
import com.dangerfield.spyfall.splash.GetGameInProgress
import com.dangerfield.spyfall.splash.GetWerewolfGameInProgress
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
        fun bindGetGameInProgress(impl: GetWerewolfGameInProgress): GetGameInProgress

        @Binds
        fun bindCheckForRequiredUpdate(impl: CheckForRequiredWerewolfUpdate): CheckForRequiredUpdate
    }
}
