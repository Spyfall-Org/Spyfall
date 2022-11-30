package com.dangerfield.spyfall.splash

class CheckForRequiredWerewolfUpdate : CheckForRequiredUpdate {
    override suspend fun shouldRequireUpdate(): Boolean {
        return false
    }
}