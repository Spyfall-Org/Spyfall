package com.dangerfield.libraries.session

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow(): Flow<User>
    suspend fun updateDarkModeConfig(darkModeConfig: DarkModeConfig)
    suspend fun updateColorConfig(colorConfig: ColorConfig)

    suspend fun addUsersGameResult(
        wasOddOneOut: Boolean,
        didWin: Boolean,
        accessCode: String,
        startedAt: Long,
    )

    suspend fun addGamePlayed(
        accessCode: String,
        startedAt: Long,
        wasSingleDevice: Boolean
    )
}