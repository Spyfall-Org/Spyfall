package com.dangerfield.libraries.storage.internal

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dangerfield.libraries.game.storage.PackAccessRecordEntity
import com.dangerfield.libraries.game.storage.PackDao
import com.dangerfield.libraries.game.storage.PackEntity
import com.dangerfield.libraries.game.storage.PackItemEntity
import com.dangerfield.libraries.session.storage.MeGamePlayed
import com.dangerfield.libraries.session.storage.MeGameResult
import com.dangerfield.libraries.session.storage.MeGameStatsDao
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos

@Database(
    entities = [
        MeGameResult::class,
        MeGamePlayed::class,
        PackEntity::class,
        PackAccessRecordEntity::class,
        PackItemEntity::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    version = 2,
)
@AutoProvideDaos
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val meGameStatsDao: MeGameStatsDao
    abstract val packDao: PackDao
}
