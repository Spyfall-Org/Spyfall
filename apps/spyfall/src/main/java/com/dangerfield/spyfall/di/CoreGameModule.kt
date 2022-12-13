package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.splash.forcedupdate.AppUpdateDataSource
import com.dangerfield.spyfall.splash.splash.GetGameInProgress
import com.dangerfield.spyfall.splash.spyfall.GetSpyfallGameInProgress
import com.dangerfield.spyfall.splash.spyfall.SpyfallAppUpdateDataSource
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
        fun bindAppUpdateDataSource(impl: SpyfallAppUpdateDataSource): AppUpdateDataSource
    }
}
