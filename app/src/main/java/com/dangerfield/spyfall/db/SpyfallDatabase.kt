package com.dangerfield.spyfall.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [], version = 1, exportSchema = false)
abstract class SpyfallDatabase : RoomDatabase() {
    abstract fun mainDao(): SpyfallDao

    companion object {
        @Volatile private var instance: SpyfallDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            SpyfallDatabase::class.java, "spyfall.db")
            .build()
    }
}