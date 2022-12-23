package com.dangerfield.werewolf.di

import com.dangerfield.spyfall.splash.forcedupdate.AppUpdateDataSource
import com.dangerfield.spyfall.splash.splash.GetGameInProgress
import com.dangerfield.spyfall.splash.werewolf.GetWerewolfGameInProgress
import com.dangerfield.spyfall.splash.werewolf.WerewolfAppUpdateDataSource
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
        fun bindAppUpdateDataSource(impl: WerewolfAppUpdateDataSource): AppUpdateDataSource
    }
}