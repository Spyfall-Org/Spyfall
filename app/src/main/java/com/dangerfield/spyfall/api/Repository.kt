package com.dangerfield.spyfall.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.BuildConfig
import com.dangerfield.spyfall.ui.joinGame.JoinGameError
import com.dangerfield.spyfall.models.*
import com.dangerfield.spyfall.ui.game.PlayAgainError
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.ui.newGame.NewGameError
import com.dangerfield.spyfall.ui.newGame.PackDetailsError
import com.dangerfield.spyfall.ui.waiting.LeaveGameError
import com.dangerfield.spyfall.util.*
import com.dangerfield.spyfall.ui.waiting.NameChangeError
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class Repository(
    private var db: FirebaseFirestore,
    private val constants: Constants,
    private val sessionListenerHelper: SessionListenerHelper,
    private val preferencesHelper: PreferencesHelper
) : GameRepository, SessionListener {

    private var createGameJob: Job? = null
    private var joinGameJob: Job? = null
    private var changeNameJob: Job? = null
    private var startGameJob: Job? = null

    override fun cancelCreateGame() = createGameJob?.cancel()
    override fun cancelJoinGame() = joinGameJob?.cancel()
    override fun cancelChangeName() = changeNameJob?.cancel()
    override fun cancelStartGame() = startGameJob?.cancel()


    /**
     * Job used to tie the coroutine context of cancellable operations to
     */

    ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Events that more than one fragment must listen to are made private global
     */
    private var liveGame: MutableLiveData<Game> = MutableLiveData()
    private var sessionEndedEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    private var removeInactiveUserEvent = MutableLiveData<Event<Resource<Unit, Unit>>>()

    /**
     * Provides VMs with access to global events
     */
    override fun getLiveGame(currentSession: Session): MutableLiveData<Game> {
        addListenerIfNewGame(currentSession)
        return liveGame
    }

    override fun getSessionEnded() = sessionEndedEvent
    override fun getRemoveInactiveUserEvent(): MutableLiveData<Event<Resource<Unit, Unit>>> =
        removeInactiveUserEvent

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Call back used by snapshot listener when game is null (was deleted on db)
     */
    override fun onSessionEnded() {
        cancelJobs()
        preferencesHelper.removeSavedSession()
        sessionEndedEvent.postValue(Event(Unit))
    }

    /**
     * Call back used by snapshot listener to update game
     */
    override fun onGameUpdates(game: Game) = liveGame.postValue(game)

    /**
     * Set by Receiver to determine network connection
     */
    var hasNetworkConnection: Boolean = false


    /**
     * cancels all operations with context tied to job
     */
    override fun cancelJobs() = listOf(
        createGameJob,
        joinGameJob,
        changeNameJob,
        startGameJob
    ).forEach {
        it?.cancel()
    }


    /**
     * Creates a game node on firebase
     * adds listener to update live data for game
     * @Success Returns current session
     * @Error returns NewGameError
     */
    override fun createGame(
        username: String,
        timeLimit: Long,
        chosenPacks: List<String>
    ): LiveData<Resource<Session, NewGameError>> {
        val result = MutableLiveData<Resource<Session, NewGameError>>()

        if (!Connectivity.isOnline) {
            result.value = Resource.Error(error = NewGameError.NETWORK_ERROR)
        } else {
            Log.d("Elijah", "Starting create game")

            createGameJob = CoroutineScope(IO).launch {
                try {
                    val accessCode = generateAccessCode()
                    val gameLocations = getGameLocations(chosenPacks as ArrayList<String>)
                    val game = Game(
                        gameLocations.random(),
                        chosenPacks as ArrayList<String>,
                        false,
                        arrayListOf(username),
                        arrayListOf(),
                        timeLimit,
                        gameLocations,
                        (System.currentTimeMillis() + millisecondsInSixHours) / 1000
                    )

                    val gameRef = db.collection(constants.games).document(accessCode)
                    gameRef.set(game).addOnSuccessListener {
                        val currentSession = Session(accessCode, username, game)
                        result.value = Resource.Success(currentSession)
                        preferencesHelper.saveSession(currentSession)
                    }.addOnFailureListener {
                        result.value =
                            Resource.Error(error = NewGameError.UNKNOWN_ERROR, exception = it)
                    }
                } catch (e: Exception) {
                    result.value =
                        Resource.Error(error = NewGameError.UNKNOWN_ERROR, exception = e)
                }
            }
        }

        return result
    }

    /**
     * Adds user name to games player list (no need for checks)
     * Adds listener to firebase to update game
     */
    override fun joinGame(
        accessCode: String,
        username: String
    ): LiveData<Event<Resource<Session, JoinGameError>>> {
        val result = MutableLiveData<Event<Resource<Session, JoinGameError>>>()

        if (!Connectivity.isOnline) {
            result.value = Event(Resource.Error(error = JoinGameError.NETWORK_ERROR))
        } else {

            joinGameJob = CoroutineScope(IO).launch {
                db.collection(constants.games).document(accessCode).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val playersList =
                                (document[Constants.GameFields.playerList] as ArrayList<String>)

                            when {
                                playersList.size >= 8 ->
                                    result.value =
                                        Event(Resource.Error(error = JoinGameError.GAME_HAS_MAX_PLAYERS))

                                document[Constants.GameFields.started] == true ->
                                    result.value =
                                        Event(Resource.Error(error = JoinGameError.GAME_HAS_STARTED))

                                playersList.contains(username) ->
                                    result.value =
                                        Event(Resource.Error(error = JoinGameError.NAME_TAKEN))

                                else -> {
                                    addPlayer(username, accessCode).addOnSuccessListener {
                                        val game = document.toObject(Game::class.java)
                                        if (game != null) {
                                            val currentSession = Session(accessCode, username, game)
                                            result.value = Event(Resource.Success(currentSession))
                                            preferencesHelper.saveSession(currentSession)
                                        } else {
                                            result.value =
                                                Event(Resource.Error(error = JoinGameError.UNKNOWN_ERROR))
                                        }
                                    }.addOnFailureListener {
                                        result.value =
                                            Event(
                                                Resource.Error(
                                                    error = JoinGameError.COULD_NOT_JOIN,
                                                    exception = it
                                                )
                                            )
                                    }
                                }
                            }
                        } else {
                            result.value =
                                Event(Resource.Error(error = JoinGameError.GAME_DOES_NOT_EXIST))
                        }
                    }.addOnFailureListener {
                        result.value = Event(Resource.Error(error = JoinGameError.NETWORK_ERROR))
                    }
            }
        }

        return result
    }

    /**
     * removes user name from games player list on db
     * posts to leave game event that both the waiting screen and game screen listen for
     */
    override fun leaveGame(currentSession: Session): MutableLiveData<Event<Resource<Unit, LeaveGameError>>> {
        val result = MutableLiveData<Event<Resource<Unit, LeaveGameError>>>()
        val gameRef = db.collection(constants.games).document(currentSession.accessCode)
        gameRef.update(
            Constants.GameFields.playerList,
            FieldValue.arrayRemove(currentSession.currentUser)
        ).addOnSuccessListener {
            sessionListenerHelper.removeListener()
            preferencesHelper.removeSavedSession()
            result.value = Event(Resource.Success(Unit))
        }.addOnFailureListener {
            result.value =
                Event(Resource.Error(error = LeaveGameError.UNKNOWN_ERROR, exception = it))
        }
        return result
    }

    override fun removeInactiveUser(currentSession: Session) {
        val gameRef = db.collection(constants.games).document(currentSession.accessCode)
        gameRef.update(
            Constants.GameFields.playerList,
            FieldValue.arrayRemove(currentSession.currentUser)
        ).addOnSuccessListener {
            sessionListenerHelper.removeListener()
            preferencesHelper.removeSavedSession()
            removeInactiveUserEvent.value = Event(Resource.Success(Unit))
        }.addOnFailureListener {
            removeInactiveUserEvent.value =
                Event(Resource.Error(error = Unit, exception = it))
        }
    }

    override fun reassignRoles(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>> {
        val result = MutableLiveData<Event<Resource<Unit, StartGameError>>>()
        CoroutineScope(IO).launch {
            val gameRef = db.collection(constants.games).document(currentSession.accessCode)
            try {
                val newLocation =
                    currentSession.game.locationList.filter { it != currentSession.game.chosenLocation }
                        .random()
                val newSession = currentSession.copy()
                newSession.game.chosenLocation = newLocation
                val roles = getRoles(newSession)
                assignRoles(roles, newSession, gameRef)
                db.collection(constants.games).document(currentSession.accessCode)
                    .update(Constants.GameFields.chosenLocation, newLocation).await()
                result.postValue(Event(Resource.Success(Unit)))
            } catch (e: Exception) {
                result.postValue(
                    Event(
                        Resource.Error(
                            error = StartGameError.Unknown,
                            exception = e
                        )
                    )
                )
            }
        }
        return result
    }


    /**
     * removes node on fire store
     * snapshot listener causes session to end
     */
    override fun endGame(currentSession: Session): MutableLiveData<Resource<Unit, Exception>> {
        val result = MutableLiveData<Resource<Unit, Exception>>()
        val gameRef = db.collection(constants.games).document(currentSession.accessCode)
        CoroutineScope(IO).launch {
            try {
                gameRef.delete().await()
                result.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                result.postValue(Resource.Error(error = e))
            } finally {
                preferencesHelper.removeSavedSession()
            }
        }

        return result
    }

    /**
     * assigns all players roles in the player objects list
     * increments statistics for games played
     */
    override fun startGame(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>> {
        val result = MutableLiveData<Event<Resource<Unit, StartGameError>>>()

        startGameJob = CoroutineScope(IO).launch {
            val gameRef = db.collection(constants.games).document(currentSession.accessCode)
            gameRef.update(Constants.GameFields.started, true)
            try {
                val roles = getRoles(currentSession)
                assignRoles(roles, currentSession, gameRef)
                result.postValue(Event(Resource.Success(Unit)))
                incrementGamesPlayed()
            } catch (e: Exception) {
                result.postValue(
                    Event(
                        Resource.Error(
                            error = StartGameError.Unknown,
                            exception = e
                        )
                    )
                )
            }
        }
        return result
    }

    /**
     * Resets relevant game data to trigger play again action
     */
    override fun resetGame(currentSession: Session): MutableLiveData<Resource<Unit, PlayAgainError>> {
        val result = MutableLiveData<Resource<Unit, PlayAgainError>>()
        val accessCode = currentSession.accessCode
        currentSession.game.let {
            val gameRef = db.collection(constants.games).document(accessCode)
            val newLocation =
                currentSession.game.locationList.filter { location -> location != currentSession.game.chosenLocation }
                    .random()
            val newGame = Game(
                newLocation, it.chosenPacks, false,
                it.playerList, ArrayList(), it.timeLimit, it.locationList, it.expiration
            )
            gameRef.set(newGame).addOnSuccessListener {
                result.postValue(Resource.Success(Unit))
            }.addOnFailureListener { e ->
                result.postValue(Resource.Error(error = PlayAgainError.Unknown, exception = e))
            }
        }
        return result
    }

    /**
     * allows a user to update their username
     */
    override fun changeName(
        newName: String,
        currentSession: Session
    ): LiveData<Event<Resource<String, NameChangeError>>> {
        val result = MutableLiveData<Event<Resource<String, NameChangeError>>>()

        changeNameJob = CoroutineScope(IO).launch {
            val index = currentSession.game.playerList.indexOf(currentSession.currentUser)
            if (index == -1) result.postValue(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
            val copy = currentSession.game.playerList.toMutableList()
            copy[index] = newName
            if (currentSession.game.started) {
                result.postValue(Event(Resource.Error(error = NameChangeError.GAME_STARTED)))

            } else {
                val gameRef = db.collection(constants.games).document(currentSession.accessCode)
                gameRef.update(Constants.GameFields.playerList, copy).addOnSuccessListener {
                    val updatedSession =
                        Session(
                            accessCode = currentSession.accessCode,
                            previousUserName = currentSession.currentUser,
                            currentUser = newName,
                            game = currentSession.game
                        )
                    preferencesHelper.saveSession(updatedSession)
                    result.postValue(Event(Resource.Success(newName)))
                }.addOnFailureListener {
                    result.postValue(
                        Event(
                            Resource.Error(
                                error = NameChangeError.UNKNOWN_ERROR,
                                exception = it
                            )
                        )
                    )
                }
            }
        }

        return result
    }


    /**
     * fetches all packs and all locations associated with those packs.
     * Makes first element in each list the name of that pack
     * returns result
     */
    override fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>> {
        val result = MutableLiveData<Resource<List<List<String>>, PackDetailsError>>()

        if (!Connectivity.isOnline) {
            result.value = Resource.Error(error = PackDetailsError.NETWORK_ERROR)
            return result
        }

        val list = mutableListOf<List<String>>()

        db.collection(constants.packs).get()
            .addOnSuccessListener { collection ->
                collection.documents.forEach { document ->
                    val pack = listOf(document.id) + document.data!!.keys.toList()
                    list.add(pack)
                }
                result.value = Resource.Success(list)

            }.addOnFailureListener {
                result.value =
                    Resource.Error(error = PackDetailsError.UNKNOWN_ERROR, exception = it)
            }

        return result
    }

    override fun incrementGamesPlayed() {
        //this function is used to keep stats about how many Android games have been played
        if (BuildConfig.DEBUG) return
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document)
            .update(Constants.StatisticsConstants.num_games_played, FieldValue.increment(1))
    }

    override fun incrementAndroidPlayers() {
        if (BuildConfig.DEBUG) return

        //this function is used to keep stats about how many Android games have been played
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document)
            .update(Constants.StatisticsConstants.num_android_players, FieldValue.increment(1))
    }

    override fun getPacks() = arrayListOf(
        GamePack(UIHelper.accentColors[0], "Standard", 1, "Standard Pack 1", false),
        GamePack(UIHelper.accentColors[1], "Standard", 2, "Standard Pack 2", false),
        GamePack(UIHelper.accentColors[2], "Special", 1, "Special Pack 1", false)
    )

    private fun clearGameLiveData() {
        liveGame = MutableLiveData()
    }

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while (db.collection(constants.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }

    private suspend fun getRoles(currentSession: Session): ArrayList<String> {
        val result = currentSession.game.chosenPacks.findFirstNonNullWhenMapped { pack ->
            db.collection(constants.packs).document(pack).get().await()
                .get(currentSession.game.chosenLocation) as ArrayList<String>?
        }
        return result ?: arrayListOf()
    }


    private suspend fun assignRoles(
        roles: ArrayList<String>,
        session: Session,
        gameRef: DocumentReference
    ) {
        if (roles.isNullOrEmpty()) {
            throw Exception()
        }

        val playerNames = session.game.playerList.shuffled()
        val playerObjectList = ArrayList<Player>()
        roles.shuffle()

        for (i in 0 until playerNames.size - 1) {
            playerObjectList.add(Player(roles[i], playerNames[i], 0))
        }

        playerObjectList.add(Player(Constants.GameFields.theSpyRole, playerNames.last(), 0))
        gameRef.update(Constants.GameFields.playerObjectList, playerObjectList.shuffled()).await()
    }

    private fun addPlayer(username: String, accessCode: String): Task<Void> {
        val gameRef = db.collection(constants.games).document(accessCode)
        return gameRef.update(Constants.GameFields.playerList, FieldValue.arrayUnion(username))
    }


    private suspend fun getGameLocations(chosenPacks: ArrayList<String>): ArrayList<String> {
        val locationList = arrayListOf<String>()
        //dictates the number locations we grab from each pack
        val numberFromEach = when (chosenPacks.size) {
            1 -> 14
            2 -> 7
            3 -> 5
            else -> 14
        }

        chosenPacks.forEach { pack ->
            val packData = db.collection(constants.packs).document(pack).get().await()
            val randomLocations =
                (packData.data?.toList()?.map { field -> field.first } ?: listOf()).shuffled()
                    .take(numberFromEach)
            locationList.addAll(randomLocations)
        }

        return locationList.take(14) as ArrayList<String>
    }

    private fun addListenerIfNewGame(currentSession: Session) {
        val creatingNewGame = !sessionListenerHelper.isListening()
        if (creatingNewGame) {
            clearGameLiveData()
            sessionListenerHelper.addListener(this, currentSession)
        }
    }

    companion object {
        private const val millisecondsInSixHours = 21600000
    }
}