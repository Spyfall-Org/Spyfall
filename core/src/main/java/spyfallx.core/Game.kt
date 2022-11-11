package spyfallx.core

data class Game(var chosenLocation: String,
                var chosenPacks: ArrayList<String>,
                var started: Boolean,
                var playerList: ArrayList<String>,
                var playerObjectList: ArrayList<Player>,
                var timeLimit: Long,
                var locationList: ArrayList<String>,
                var expiration: Long

)