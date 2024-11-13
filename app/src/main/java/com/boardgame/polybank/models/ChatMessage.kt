package com.boardgame.polybank.models

data class ChatMessage(
    val name: String, // Nombre del remitente
    val content: String, // Contenido del mensaje
    val type: String // Tipo del mensaje
)