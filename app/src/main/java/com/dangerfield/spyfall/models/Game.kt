package com.dangerfield.spyfall.models

class Game(val chosenLocation: String,val chosenPacks: ArrayList<String>, val isStarted: Boolean,val playerList: ArrayList<String>, val playerObjectList: ArrayList<Player>,val timeLimit: Double){
    constructor() : this("", ArrayList<String>(),false,ArrayList<String>(),ArrayList<Player>(),0.0)

}