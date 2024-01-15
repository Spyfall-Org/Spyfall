package com.dangerfield.libraries.storage.internal

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dangerfield.libraries.session.GameKey
import com.squareup.moshi.Moshi
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.readJson
import spyfallx.core.toJson
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val moshi: Moshi
) {

    @TypeConverter
    fun fromGameKey(key: GameKey?): String? {
        return Try { moshi.toJson(key!!) }
            .logOnError()
            .getOrNull()
    }

    @TypeConverter
    fun toGameKey(string: String?): GameKey? {
        return Try {
            moshi.readJson<GameKey>(string!!)
        }
            .logOnError()
            .getOrNull()
    }
}