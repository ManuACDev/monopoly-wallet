package com.boardgame.polybank.models

data class GameConfig(
    val gameId: String,
    val numPlayers: Int,
    val initialMoney: Int,
    val passGoMoney: Int,
    val isBankAutomatic: Boolean
) {
    // Constructor vacío necesario para Firebase
    constructor() : this("",0, 0, 0, false)
}