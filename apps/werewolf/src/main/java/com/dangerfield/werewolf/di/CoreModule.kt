package com.dangerfield.werewolf.di

import android.content.Context
import android.content.SharedPreferences
import com.dangerfield.spyfall.splash.forcedupdate.AppUpdateDataSource
import com.dangerfield.spyfall.splash.werewolf.WerewolfAppUpdateDataSource
import com.dangerfield.werewolf.BuildConfig.CONFIG_COLLECTION_KEY
import com.dangerfield.werewolf.BuildConfig.VERSION_CODE
import com.dangerfield.werewolf.BuildConfig.VERSION_NAME
import com.dangerfield.werewolf.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
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
    fun provideBuildInfo(): BuildInfo = BuildInfo(
        TargetApp.Werewolf,
        VERSION_CODE,
        VERSION_NAME,
        CONFIG_COLLECTION_KEY
    )

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()
}
