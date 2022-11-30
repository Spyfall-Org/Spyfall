package com.dangerfield.spyfall.splash


interface CheckForRequiredUpdate {
    suspend fun shouldRequireUpdate(): Boolean
}
