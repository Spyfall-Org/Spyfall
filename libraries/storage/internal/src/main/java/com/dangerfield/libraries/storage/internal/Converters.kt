package com.dangerfield.libraries.storage.internal

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dangerfield.libraries.session.GameKey
import com.squareup.moshi.Moshi
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.readJson
import oddoneout.core.toJson
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val moshi: Moshi
) {

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
}