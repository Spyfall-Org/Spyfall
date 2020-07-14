package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.BuildConfig
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.*
import com.dangerfield.spyfall.newGame.NewGameError
import com.dangerfield.spyfall.newGame.PackDetailsError
import com.dangerfield.spyfall.util.*
import com.dangerfield.spyfall.waiting.NameChangeError
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import java.util.*

class Repository(override var db: FirebaseFirestore, val constants: Constants) : GameRepository(), SessionEndListener {


    /**
     * job used to tie all coroutines to in order to cancel
     */
    private var job: Job = Job()

    /**
     * Holds info for current game being played. Set when joining game
     * nulled when leaving or ending game
     */
    override var currentSession: CurrentSession? = null

    override fun onSessionEnded() { currentSession = null }

    /**
     * Set by Receiver to determine network connection
     */
    var hasNetworkConnection: Boolean = false

    /**
     * Creates a game node on firebase
     * Returns Access code to that node
     */
    override fun createGame(username: String, timeLimit: Long, chosenPacks: List<String>): LiveData<Resource<Unit, NewGameError>> {
        val result = MutableLiveData<Resource<Unit, NewGameError>>()

        if(!Connectivity.isOnline){
            result.value  = Resource.Error(error = NewGameError.NETWORK_ERROR)
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
                    result.value = Resource.Success(Unit)
                    currentSession = CurrentSession(accessCode, username, sessionEndListener = this@Repository)
                        .withListener(gameRef)
                }.addOnFailureListener {
                    result.value  = Resource.Error(error = NewGameError.UNKNOWN_ERROR)
                }
            }
        }

        return result
    }

    private suspend fun getGameLocations(chosenPacks: ArrayList<String>) : ArrayList<String> {
        val locationList = arrayListOf<String>()
        //dictates the number locations we grab from each pack
        val numberFromEach = when(chosenPacks.size) {
            1 -> 14
            2 -> 7
            3 -> 5
            else -> 14
        }

        chosenPacks.forEach {pack ->
            val packData = db.collection("packs").document(pack).get().await()
            val randomLocations = (packData.data?.toList()?.map { field -> field.first } ?: listOf()).shuffled().take(numberFromEach)
            locationList.addAll(randomLocations)
        }

        return locationList.take(14) as ArrayList<String>
    }

    /**
     * Adds user name to games player list (no need for checks)
     * Adds listener to firebase to update game
     */
    override fun joinGame(accessCode: String, username: String): LiveData<Resource<Unit, JoinGameError>>  {
        val result = MutableLiveData<Resource<Unit, JoinGameError>>()

        if(!Connectivity.isOnline){
            result.value  = Resource.Error(error = JoinGameError.NETWORK_ERROR)
        } else {
            db.collection(constants.games).document(accessCode).get().addOnSuccessListener { game ->

                if(game.exists()){
                    val list = (game["playerList"] as ArrayList<String>)

                    when {
                        list.size >= 8 ->
                            result.value = Resource.Error(error = JoinGameError.GAME_HAS_MAX_PLAYERS)

                        game["started"] == true ->
                            result.value = Resource.Error(error = JoinGameError.GAME_HAS_STARTED)

                        list.contains(username) ->
                            result.value = Resource.Error(error = JoinGameError.NAME_TAKEN)

                        else -> {
                            addPlayer(username, accessCode).addOnSuccessListener {
                                Crashlytics.log("Player: \"$username\" has joined $accessCode")
                                val gameRef: DocumentReference = db.collection(constants.games).document(accessCode)
                                currentSession = CurrentSession(accessCode, username, sessionEndListener = this)
                                    .withListener(gameRef)
                                result.value = Resource.Success(Unit)
                            }.addOnFailureListener {
                                Crashlytics.log("Player: \"$username\" was unable to join $accessCode")
                                result.value = Resource.Error(error = JoinGameError.COULD_NOT_JOIN)
                            }
                        }
                    }
                }else{
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
     * removes user name from games player list
     * current session ends itself
     */
    override fun leaveGame() {
        currentSession?.let { session ->
            val gameRef = db.collection(constants.games).document(session.accessCode)
            gameRef.update("playerList", FieldValue.arrayRemove(session.currentUser))
        }
    }

    /**
     * removes node on fire store
     * current session ends itself
     */
    override fun endGame() {
        currentSession?.let {session ->
            val gameRef = db.collection(constants.games).document(session.accessCode)
            gameRef.delete()
        }
    }

    override fun startGame() {
        currentSession?.let {session ->
            if(session.isBeingStarted()){ return }
            CoroutineScope(IO + job).launch {
                val gameRef = db.collection(constants.games).document(session.accessCode)
                gameRef.update("started", true)
                val roles = getRoles(session)
                assignRoles(roles, session, gameRef)
                incrementGamesPlayed()
            }
        }
    }

    private suspend fun getRoles(currentSession: CurrentSession): ArrayList<String> {
        val result = currentSession.getLiveGame().value?.let {game ->
             game.chosenPacks.findFirstNonNullWhenMapped {pack ->
                db.collection("packs").document(pack).get().await().get(game.chosenLocation) as ArrayList<String>?
            }
        }
        return result ?: arrayListOf()
    }


    private suspend fun assignRoles(roles: ArrayList<String>, session: CurrentSession, gameRef: DocumentReference) {
        if(roles.isNullOrEmpty()){ return }

        session.getLiveGame().value?.let {game ->
            val playerNames = game.playerList.shuffled()
            val playerObjectList = ArrayList<Player>()
            roles.shuffle()

            for (i in 0 until playerNames.size - 1) {
                playerObjectList.add(Player(roles[i], playerNames[i], 0))
            }

            playerObjectList.add(Player("The Spy!", playerNames.last(), 0))
            gameRef.update("playerObjectList", playerObjectList.shuffled()).await()
        }
    }


    override fun resetGame() {
        // resets variables on firebase for play again, which will update viewmodel
        val accessCode = currentSession?.accessCode ?: return

        currentSession?.getLiveGame()?.value?.let {
            val gameRef = db.collection(constants.games).document(accessCode)

            val newLocation = it.locationList.random()
            val newGame = Game(newLocation, it.chosenPacks,false,
                it.playerList, ArrayList(),it.timeLimit, it.locationList )
            gameRef.set(newGame)
        }
    }

    override fun changeName(newName: String): LiveData<Event<Resource<String, NameChangeError>>> {
        val result =  MutableLiveData<Event<Resource<String, NameChangeError>>>()

        currentSession?.let {session ->

            session.getGameValue()?.let { game ->
                val index = game.playerList.indexOf(session.currentUser)
                if (index == -1 ) result.postValue(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
                game.playerList[index] = newName
                session.currentUser = newName

                val gameRef = db.collection(constants.games).document(session.accessCode)
                gameRef.update("playerList", game.playerList).addOnSuccessListener {
                    result.postValue(Event(Resource.Success(newName)))
                }.addOnFailureListener {
                    result.postValue(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
                }
            }
        }

        return result
    }


    override fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>> {
        val result = MutableLiveData<Resource<List<List<String>>, PackDetailsError>>()

        if(!Connectivity.isOnline) {
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
                result.value = Resource.Error(error = PackDetailsError.UNKNOWN_ERROR)
            }

        return result
    }

    private suspend fun generateAccessCode(): String {
        var newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        while(db.collection(constants.games).document(newCode).get().await().exists()) {
            newCode = UUID.randomUUID().toString().substring(0, 6).toLowerCase()
        }
        return newCode
    }

    override fun incrementGamesPlayed(){
        //this function is used to keep stats about how many Android games have been played
        if(BuildConfig.DEBUG == true) return
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document).update(Constants.StatisticsConstants.num_games_played,FieldValue.increment(1))
    }

    override fun incrementAndroidPlayers(){
        if(BuildConfig.DEBUG == true) return

        //this function is used to keep stats about how many Android games have been played
        db.collection(Constants.StatisticsConstants.collection)
            .document(Constants.StatisticsConstants.document).update(Constants.StatisticsConstants.num_android_players,FieldValue.increment(1))
    }

    override fun getPacks()
            = arrayListOf(
        GamePack(UIHelper.accentColors[0],"Standard",1,"Standard Pack 1",false),
        GamePack(UIHelper.accentColors[1],"Standard",2,"Standard Pack 2",false),
        GamePack(UIHelper.accentColors[2],"Special",1,"Special Pack 1",false)
    )
}