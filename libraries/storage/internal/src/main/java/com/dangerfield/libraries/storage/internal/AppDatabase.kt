package com.dangerfield.libraries.storage.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dangerfield.libraries.session.storage.MeGamePlayed
import com.dangerfield.libraries.session.storage.MeGameResult
import com.dangerfield.libraries.session.storage.MeGameStatsDao
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos

@Database(
    entities = [
        MeGameResult::class,
        MeGamePlayed::class
    ],
    version = 1,
    exportSchema = true
)
@AutoProvideDaos
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val meGameStatsDao: MeGameStatsDao
}
