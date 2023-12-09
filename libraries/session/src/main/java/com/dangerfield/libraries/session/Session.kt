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
    val user: User
    val startedAt: Long?
    val activeGame: ActiveGame?
    val sessionId: Long?
}

@JsonClass(generateAdapter = true)
data class ActiveGame(
    val accessCode: String,
    val userId: String
)
