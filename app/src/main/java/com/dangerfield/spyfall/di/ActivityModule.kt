package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.navigation.InternalSplashNavigator
import com.dangerfield.spyfall.navigation.InternalWelcomeNavigator
import com.dangerfield.spyfall.splash.splash.SplashNavigator
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface ActivityModule {
    @Binds
    fun bindWelcomeNavigator(impl: InternalWelcomeNavigator): WelcomeNavigator

    @Binds
    fun bindSplashNavigator(impl: InternalSplashNavigator): SplashNavigator
}
