package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.legacy.ui.forcedupdate.AppUpdateDataSource
import com.dangerfield.spyfall.legacy.ui.splash.GetGameInProgress
import com.dangerfield.spyfall.legacy.api.GetSpyfallGameInProgress
import com.dangerfield.spyfall.legacy.api.SpyfallAppUpdateDataSource
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
