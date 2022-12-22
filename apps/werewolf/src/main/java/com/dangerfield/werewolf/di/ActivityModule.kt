package com.dangerfield.werewolf.di

import com.dangerfield.spyfall.splash.splash.SplashNavigator
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import com.dangerfield.werewolf.navigation.InternalSplashNavigator
import com.dangerfield.werewolf.navigation.InternalWelcomeNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface ActivityModule {

    @Binds
    fun bindSplashNavigator(impl: InternalSplashNavigator): SplashNavigator

    @Binds
    fun bindWelcomeNavigator(impl: InternalWelcomeNavigator): WelcomeNavigator
}
