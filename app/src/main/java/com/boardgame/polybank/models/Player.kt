package com.boardgame.polybank.models

data class Player(
    val name: String, // Nombre del jugador
    val money: Long, // Dinero del jugador
    val uid: String, // Uid del jugador
    val admin: Boolean, // Permisos jugador
    val banker: Boolean // Propiedad Banquero
)