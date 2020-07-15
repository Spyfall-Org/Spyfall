package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.BuildConfig
import com.dangerfield.spyfall.ui.joinGame.JoinGameError
import com.dangerfield.spyfall.models.*
import com.dangerfield.spyfall.ui.newGame.NewGameError
import com.dangerfield.spyfall.ui.newGame.PackDetailsError
import com.dangerfield.spyfall.util.*
import com.dangerfield.spyfall.ui.waiting.NameChangeError
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import java.util.*

class Repository(
    private var db: FirebaseFirestore,
    private val constants: Constants,
    private val sessionListenerHelper: SessionListenerHelper
) : GameRepository, SessionListener {

    private var job: Job = Job()
    private var liveGame: MutableLiveData<Game> = MutableLiveData()
    private var sessionEnded: MutableLiveData<Event<Unit>> = MutableLiveData()

    /**
     * All fragments listen to this live data to update views with new game data
     */
    override fun getLiveGame() = liveGame

    /**
     * All fragments listen to this live data determine when to go back to start
     */
    override fun getSessionEnded() = sessionEnded

    /**
     * Call back used by snapshot listener when game is null (was deleted on db)
     * or current user was removed
     */
    override fun onSessionEnded() {
        job.cancel()
        sessionEnded.postValue(Event(Unit))
    }

    /**
     * Call back used by snapshot listener to update game
     */
    override fun onGameUpdates(game: Game) {
        liveGame.postValue(game)
    }

    /**
     * Set by Receiver to determine network connection
     */
    var hasNetworkConnection: Boolean = false

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
    ): LiveData<Resource<CurrentSession, NewGameError>> {
        val result = MutableLiveData<Resource<CurrentSession, NewGameError>>()

        if (!Connectivity.isOnline) {
            result.value = Resource.Error(error = NewGameError.NETWORK_ERROR)
        } else {
            CoroutineScope(IO + job).launch {
                val accessCode = generateAccessCode()
                val gameLocations = getGameLocations(chosenPacks as ArrayList<String>)
                val game = Game(
                    gameLocations.random(),
                    chosenPacks,
                    false,
                    arrayListOf(username),
                    arrayListOf(),
                    timeLimit,
                    gameLocations
                )

                val gameRef = db.collection(constants.games).document(accessCode)

                gameRef.set(game).addOnSuccessListener {
                    val currentSession = CurrentSession(accessCode, username, game)
                    result.value = Resource.Success(currentSession)
                    sessionListenerHelper.addListener(this@Repository, currentSession)
                }.addOnFailureListener {
                    result.value = Resource.Error(error = NewGameError.UNKNOWN_ERROR, exception = it)
                }
            }
        }

        return result
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

    /**
     * Adds user name to games player list (no need for checks)
     * Adds listener to firebase to update game
     */
    override fun joinGame(
        accessCode: String,
        username: String
    ): LiveData<Resource<CurrentSession, JoinGameError>> {
        val result = MutableLiveData<Resource<CurrentSession, JoinGameError>>()

        if (!Connectivity.isOnline) {
            result.value = Resource.Error(error = JoinGameError.NETWORK_ERROR)
        } else {
            db.collection(constants.games).document(accessCode).get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {
                        val list = (document["playerList"] as ArrayList<String>)

                        when {
                            list.size >= 8 ->
                                result.value =
                                    Resource.Error(error = JoinGameError.GAME_HAS_MAX_PLAYERS)

                            document["started"] == true ->
                                result.value =
                                    Resource.Error(error = JoinGameError.GAME_HAS_STARTED)

                            list.contains(username) ->
                                result.value = Resource.Error(error = JoinGameError.NAME_TAKEN)

                            else -> {
                                addPlayer(username, accessCode).addOnSuccessListener {
                                    val game = document.toObject(Game::class.java)
                                    if (game != null) {
                                        val currentSession =
                                            CurrentSession(accessCode, username, game)
                                        result.value = Resource.Success(currentSession)
                                        sessionListenerHelper.addListener(this, currentSession)
                                    } else {
                                        result.value =
                                            Resource.Error(error = JoinGameError.UNKNOWN_ERROR)
                                    }
                                }.addOnFailureListener {
                                    result.value =
                                        Resource.Error(error = JoinGameError.COULD_NOT_JOIN, exception = it)
                                }
                            }
                        }
                    } else {
                        result.value = Resource.Error(error = JoinGameError.GAME_DOES_NOT_EXIST)
                    }
                }
        }

        return result
    }

    private fun addPlayer(username: String, accessCode: String): Task<Void> {
        val gameRef = db.collection(constants.games).document(accessCode)
        return gameRef.update("playerList", FieldValue.arrayUnion(username))
    }

    /**
     * removes user name from games player list on db
     * snapshot listener causes session to end
     */
    override fun leaveGame(currentSession: CurrentSession) {
        val gameRef = db.collection(constants.games).document(currentSession.accessCode)
        gameRef.update("playerList", FieldValue.arrayRemove(currentSession.currentUser))
    }

    /**
     * removes node on fire store
     * snapshot listener causes session to end
     */
    override fun endGame(currentSession: CurrentSession) {
        val gameRef = db.collection(constants.games).document(currentSession.accessCode)
        gameRef.delete()
    }

    /**
     * assigns all players roles in the player objects list
     * increments statistics for games played
     */
    override fun startGame(currentSession: CurrentSession) {
        if (currentSession.isBeingStarted()) {
            return
        }
        CoroutineScope(IO + job).launch {
            val gameRef = db.collection(constants.games).document(currentSession.accessCode)
            gameRef.update("started", true)
            val roles = getRoles(currentSession)
            assignRoles(roles, currentSession, gameRef)
            incrementGamesPlayed()
        }
    }

    private suspend fun getRoles(currentSession: CurrentSession): ArrayList<String> {
        val result = currentSession.game.chosenPacks.findFirstNonNullWhenMapped { pack ->
            db.collection(constants.packs).document(pack).get().await()
                .get(currentSession.game.chosenLocation) as ArrayList<String>?
        }
        return result ?: arrayListOf()
    }


    private suspend fun assignRoles(
        roles: ArrayList<String>,
        session: CurrentSession,
        gameRef: DocumentReference
    ) {
        if (roles.isNullOrEmpty()) {
            return
        }

        val playerNames = session.game.playerList.shuffled()
        val playerObjectList = ArrayList<Player>()
        roles.shuffle()

        for (i in 0 until playerNames.size - 1) {
            playerObjectList.add(Player(roles[i], playerNames[i], 0))
        }

        playerObjectList.add(Player("The Spy!", playerNames.last(), 0))
        gameRef.update("playerObjectList", playerObjectList.shuffled()).await()

    }


    /**
     * Resets relevant game data to trigger play again action
     */
    override fun resetGame(currentSession: CurrentSession) {
        // resets variables on firebase for play again, which will update viewmodel
        val accessCode = currentSession.accessCode
        currentSession.game.let {
            val gameRef = db.collection(constants.games).document(accessCode)

            val newLocation = currentSession.game.locationList.random()
            val newGame = Game(
                newLocation, it.chosenPacks, false,
                it.playerList, ArrayList(), it.timeLimit, it.locationList
            )
            gameRef.set(newGame)
        }
    }

    /**
     * allows a user to update their username
     */
    override fun changeName(
        newName: String,
        currentSession: CurrentSession
    ): LiveData<Event<Resource<String, NameChangeError>>> {
        val result = MutableLiveData<Event<Resource<String, NameChangeError>>>()
        CoroutineScope(Dispatchers.Default + job).launch {
            val index = currentSession.game.playerList.indexOf(currentSession.currentUser)
            if (index == -1) result.postValue(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
            currentSession.game.playerList[index] = newName
            currentSession.currentUser = newName
            val gameRef = db.collection(constants.games).document(currentSession.accessCode)
            gameRef.update("playerList", currentSession.game.playerList).addOnSuccessListener {
                result.postValue(Event(Resource.Success(newName)))
            }.addOnFailureListener {
                result.postValue(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR, exception = it)))
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
                result.value = Resource.Error(error = PackDetailsError.UNKNOWN_ERROR, exception = it)
            }

        return result
    }

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while (db.collection(constants.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }

    override fun incrementGamesPlayed() {
        //this function is used to keep stats about how many Android games have been played
        if (BuildConfig.DEBUG == true) return
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document)
            .update(Constants.StatisticsConstants.num_games_played, FieldValue.increment(1))
    }

    override fun incrementAndroidPlayers() {
        if (BuildConfig.DEBUG == true) return

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
}