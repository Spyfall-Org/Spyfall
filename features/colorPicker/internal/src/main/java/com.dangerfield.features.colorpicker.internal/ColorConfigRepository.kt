package com.dangerfield.features.colorpicker.internal

import androidx.datastore.core.DataStore
import com.dangerfield.features.colorpicker.ColorConfig
import com.dangerfield.features.colorpicker.ColorConfigRepository
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class DefaultColorConfigRepository @Inject constructor(
    private val dataStore: DataStore<ColorConfig>
): ColorConfigRepository {
    override fun getColorConfigFlow(): Flow<ColorConfig> = dataStore.data

    override suspend fun setColorConfig(colorConfig: ColorConfig) {
        dataStore.tryUpdateData { colorConfig }
    }
}
