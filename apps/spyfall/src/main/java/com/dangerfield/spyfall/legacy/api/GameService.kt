package com.dangerfield.spyfall.legacy.api

import com.dangerfield.spyfall.legacy.models.Game
import com.dangerfield.spyfall.legacy.models.Player
import com.google.android.gms.tasks.Task

interface GameService {
    fun setGame(accessCode: String, game: Game) : Task<Void>
    fun getGame(accessCode: String) : Task<Game?>
    fun removePlayer(accessCode: String, player: String) : Task<Void>
    fun addPlayer(accessCode: String, player: String) : Task<Void>
    fun updateChosenLocation(accessCode: String, newLocation: String) : Task<Void>
    fun endGame(accessCode: String) : Task<Void>
    fun setStarted(accessCode: String, started: Boolean) : Task<Void>
    fun setPlayerList(accessCode: String, list: List<String>) : Task<Void>
    fun getPackDetails() : Task<List<List<String>>?>
    fun incrementNumAndroidPlayers()
    fun incrementNumGamesPlayed()
    fun accessCodeExists(code: String) : Task<Boolean>
    suspend fun findRolesForLocationInPacks(packs: List<String>, chosenLocation: String): Task<List<String>?>
    fun setPlayerObjectsList(accessCode: String, list: List<Player>) : Task<Void>
    fun getLocationsFromPack(pack: String, numberOfLocations: Int) : Task<List<String>?>

}