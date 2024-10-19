package com.example.wallet.models

data class GameConfig(
    val numPlayers: Int,
    val initialMoney: Int,
    val passGoMoney: Int,
    val isBankAutomatic: Boolean
)