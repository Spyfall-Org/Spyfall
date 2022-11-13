package com.dangerfield.spyfall.di.hiltModules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import spyfallx.coregame.SpyfallRepository
import spyfallx.coregameapi.GameRepository


@Module(includes = [CoreGameModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object CoreGameModule {

    @Module
    @DisableInstallInCheck
    interface Bindings {
        @Binds
        fun bindGameRepo(impl: SpyfallRepository) : GameRepository
    }
}
