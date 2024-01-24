package com.dangerfield.spyfall.di

import android.content.Context
import android.content.SharedPreferences
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.ads.ui.InterstitialAd
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.DispatcherProvider
import com.dangerfield.spyfall.free.BuildConfig
import com.dangerfield.spyfall.free.BuildConfig.VERSION_CODE
import com.dangerfield.spyfall.free.BuildConfig.VERSION_NAME
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import oddoneout.core.BuildInfo
import oddoneout.core.BuildType
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    private const val SHARED_PREFS_KEY = "com.dangerfield.oddoneout.shared_prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SHARED_PREFS_KEY,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providesFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun providesGameResetInterstitialAd(
        adsConfig: AdsConfig,
        @ApplicationScope applicationScope: CoroutineScope,
        dispatcherProvider: DispatcherProvider
    ): InterstitialAd<OddOneOutAd.GameRestartInterstitial> {
        return InterstitialAd(
            ad = OddOneOutAd.GameRestartInterstitial,
            adsConfig = adsConfig,
            applicationScope = applicationScope,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Provides
    fun provideBuildInfo(@ApplicationContext context: Context): BuildInfo =
        BuildInfo(
            versionCode = VERSION_CODE,
            versionName = VERSION_NAME,
            packageName = context.packageName,
            buildType = when {
                BuildConfig.DEBUG -> BuildType.DEBUG
                BuildConfig.IS_QA -> BuildType.QA
                else -> BuildType.RELEASE
            }
        )

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @ApplicationScope
    @Singleton
    fun providesApplicationScope(dispatcherProvider: DispatcherProvider): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.default)
}
