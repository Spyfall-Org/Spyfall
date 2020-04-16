package com.dangerfield.spyfall.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.util.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class Repository(override var db: FirebaseFirestore) : GameRepository() {

    /**
     * job used to tie all coroutines to in order to cancel
     */
    private var job: Job = Job()

    /**
     * Observable data representing the game on firebase
     * Kept up to date by firebase listener
     */
    override var game: LiveData<Game> = MutableLiveData()


    /**
     * Creates a game node on firebase
     * Returns Access code to that node
     */
    override fun createGame(chosenPacks: List<String>, timeLimit: Int, username: String): LiveData<Event<String>> {
        var result = MutableLiveData<Event<String>>()

        //TODO get access code and locations list async and await on both of them
        /*
        generate access code
        get list of all locations from chosen packs
        create game object and update ref
        set result even with access code
         */
        return result
    }

    /**
     * Adds user name to games player list (no need for checks)
     * Adds listener to firebase to update game
     */
    override fun joinGame() {

    }

    /**
     * removes user name from games player list
     * removes listener to firebase to update game
     */
    override fun leaveGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun endGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeName() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while(db.collection(Collections.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }

}