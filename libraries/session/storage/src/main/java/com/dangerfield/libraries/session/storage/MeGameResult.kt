package com.dangerfield.libraries.session.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dangerfield.libraries.session.GameKey

/**
 * Represents a game result that was played by the ME user
 * Not all game results will be collected in app because users can vote
 * amongst themselves. So we record the results seperate from the number of games
 * played. See [MeGamePlayed]
 */
@Entity(tableName = "MeGameResult")
data class MeGameResult(
    @PrimaryKey val gameKey: GameKey,
    val didWin: Boolean,
    val wasOddOne: Boolean,
    val uploadStatus: UploadStatus = UploadStatus.NOT_UPLOADED
)

/**
 * Represents a game that was played by the ME user
 * This is used to keep track of how many games the user has played (both single and multi device)
 */
@Entity(tableName = "MeGamePlayed")
data class MeGamePlayed(
    @PrimaryKey val gameKey: GameKey,
    val wasSingleDevice: Boolean,
    val uploadStatus: UploadStatus = UploadStatus.NOT_UPLOADED
)

/**
 * Game stats are kept in sync with backend
 * when adding a result to the local db we mark it as not uploaded until the upload succeeds
 * on app launch we upload all the results that are marked as not uploade
 */
enum class UploadStatus {
    NOT_UPLOADED,
    UPLOADED
}
