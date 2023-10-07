package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.legacy.navigation.InternalSplashNavigator
import com.dangerfield.spyfall.legacy.ui.splash.SplashNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface ActivityModule {

    @Binds
    fun bindSplashNavigator(impl: InternalSplashNavigator): SplashNavigator
}
