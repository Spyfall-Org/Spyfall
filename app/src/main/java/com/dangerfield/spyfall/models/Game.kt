package com.dangerfield.spyfall.models

import androidx.lifecycle.MutableLiveData

data class Game(var chosenLocation: String,
                var chosenPacks: ArrayList<String>,
                var started: Boolean,
                var playerList: ArrayList<String>,
                var playerObjectList: ArrayList<Player>,
                var timeLimit: Long){


    constructor() : this("", ArrayList<String>(),false,ArrayList<String>(),ArrayList<Player>(),0.0.toLong())

}