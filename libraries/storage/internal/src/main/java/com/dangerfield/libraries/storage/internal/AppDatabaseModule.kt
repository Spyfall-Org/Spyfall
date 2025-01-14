package com.dangerfield.libraries.storage.internal

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        converters: Converters
    ): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "odd-one-out")
            .addTypeConverter(converters)
            .build()
    }

}