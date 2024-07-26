package com.dangerfield.libraries.storage.internal

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dangerfield.libraries.game.storage.DbPackOwner
import com.dangerfield.libraries.game.storage.DbPackType
import com.dangerfield.libraries.session.GameKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.readJson
import oddoneout.core.toJson
import java.time.Instant
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val moshi: Moshi
) {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { moshi.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { moshi.readJson(value) }
    }


    @TypeConverter
    fun fromGameKey(key: GameKey?): String? {
        return Catching { moshi.toJson(key!!) }
            .logOnFailure()
            .getOrNull()
    }

    @TypeConverter
    fun toGameKey(string: String?): GameKey? {
        return Catching {
            moshi.readJson<GameKey>(string!!)
        }
            .logOnFailure()
            .getOrNull()
    }

    @TypeConverter
    fun fromDbPackType(value: DbPackType): String = value.name

    @TypeConverter
    fun toDbPackType(value: String): DbPackType = DbPackType.valueOf(value)

    @TypeConverter
    fun fromDbPackOwner(value: DbPackOwner): String = value.name

    @TypeConverter
    fun toDbPackOwner(value: String): DbPackOwner = DbPackOwner.valueOf(value)

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}