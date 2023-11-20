package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.legacy.ui.splash.GetGameInProgress
import com.dangerfield.spyfall.legacy.api.GetSpyfallGameInProgress
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
