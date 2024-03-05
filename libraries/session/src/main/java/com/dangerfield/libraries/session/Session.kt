package com.dangerfield.libraries.session

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

/**
 * Object Representing the current application session and the users state within it.
 *
 * These values should always be up to date when accessed.
 * These values should not be accessed during app startup as they a loaded in a blocking manner
 * if they have not already been loaded.
 */
interface Session {
    val startedAt: Long?
    val sessionId: Long?
    val user: User
    val activeGame: ActiveGame?
}

@JsonClass(generateAdapter = true)
// TODO need to version this, i added a new field and things crashed on the next run,
data class ActiveGame(
    val accessCode: String,
    val userId: String,
    val isSingleDevice: Boolean
)
