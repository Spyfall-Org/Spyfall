package spyfallx.coregame

import android.content.SharedPreferences
import androidx.core.content.edit
import spyfallx.core.allOrNone
import spyfallx.coregameapi.GamePreferences
import javax.inject.Inject

const val KEY_USERNAME = "SPYFALL_USERNAME"
const val KEY_USER_ID = "SPYFALL_USER_ID"
const val KEY_ROLE = "SPYFALL_ROLE"
const val KEY_HOST = "SPYFALL_HOST"
const val KEY_ACCESS_CODE = "SPYFALL_ACESSCODE"

class SpyfallGamePreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : GamePreferences<SpyfallSession> {

    override var session: SpyfallSession?
        get() = allOrNone(
            getPlayerInfo(),
            sharedPreferences.getString(KEY_ACCESS_CODE, null)

        ) { player, accessCode ->
            SpyfallSession(player = player, accessCode = accessCode)
        }
        set(value) {
            sharedPreferences.edit {
                putString(KEY_USERNAME, value?.player?.username)
                putString(KEY_USER_ID, value?.player?.id)
                value?.player?.isHost?.let { putBoolean(KEY_HOST, it) }
                putString(KEY_ROLE, value?.player?.role)
                putString(KEY_ACCESS_CODE, value?.accessCode)
            }
        }

    private fun getPlayerInfo() = allOrNone(
        sharedPreferences.getString(KEY_USERNAME, null),
        sharedPreferences.getString(KEY_USER_ID, null),
    ) { username, userId ->
        val role = sharedPreferences.getString(KEY_ROLE, null)
        val isHost = sharedPreferences.getBoolean(KEY_HOST, false)
        SpyfallPlayer(role = role, id = userId, username = username, isHost = isHost)
    }
}
