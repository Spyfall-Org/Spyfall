package com.dangerfield.features.colorpicker

import kotlinx.coroutines.flow.Flow

interface ColorConfigRepository {
    fun getColorConfigFlow(): Flow<ColorConfig>
    suspend fun setColorConfig(colorConfig: ColorConfig)
}
