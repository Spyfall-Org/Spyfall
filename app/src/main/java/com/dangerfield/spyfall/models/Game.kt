package com.dangerfield.spyfall.models

data class Game(var chosenLocation: String,
                var chosenPacks: ArrayList<String>,
                var isStarted: Boolean,
                var playerList: ArrayList<String>,
                var playerObjectList: ArrayList<Player>,
                var timeLimit: Long){


    constructor() : this("", ArrayList<String>(),false,ArrayList<String>(),ArrayList<Player>(),0.0.toLong())

}