package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.navigation.InternalSplashNavigator
import com.dangerfield.spyfall.navigation.InternalWelcomeNavigator
import com.dangerfield.spyfall.splash.SplashNavigator
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindWelcomeNavigator(impl: InternalWelcomeNavigator): WelcomeNavigator

    @Binds
    abstract fun bindSplashNavigator(impl: InternalSplashNavigator): SplashNavigator
}
