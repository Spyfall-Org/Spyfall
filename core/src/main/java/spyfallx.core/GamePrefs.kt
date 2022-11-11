package spyfallx.core

import android.content.SharedPreferences
import androidx.core.content.edit

private const val KEY_USERNAME = "KEY_USERNAME"
private const val KEY_ACCESS_CODE = "KEY_ACCESS_CODE"

class GamePrefs(private val sharedPreferences: SharedPreferences) {

    var session: Session?
        get() = allOrNone(
            sharedPreferences.getString(KEY_USERNAME, null),
            sharedPreferences.getString(KEY_USERNAME, null)
        ) { username, accessCode ->
            Session(user = User(username), accessCode = accessCode)
        }
        set(value) {
            sharedPreferences.edit {
                putString(KEY_USERNAME, value?.user?.username)
                putString(KEY_ACCESS_CODE, value?.accessCode)
            }
        }
}