package com.dangerfield.spyfall.di

import android.content.Context
import android.content.SharedPreferences
import com.dangerfield.spyfall.BuildConfig.VERSION_CODE
import com.dangerfield.spyfall.BuildConfig.VERSION_NAME
import com.dangerfield.spyfall.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import spyfallx.core.BuildInfo
import spyfallx.core.TargetApp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun provideBuildInfo(): BuildInfo = BuildInfo(TargetApp.SPYFALL, VERSION_CODE, VERSION_NAME)

}
