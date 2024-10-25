package com.example.wallet.services

import com.example.wallet.models.GameConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreService {

    private val firestore = FirebaseFirestore.getInstance()

    // Crear una nueva partida
    suspend fun createGame(gameId: String, data: Map<String, Any>) {
        try {
            firestore.collection("Games")
                .document(gameId)
                .set(data)
                .await()
        } catch (e: Exception) {
            // Manejar errores
            println("Error al crear la partida")
            throw e
        }
    }

    // Unirse a una partida
    suspend fun joinGame(gameId: String, playerName: String) {
        try {
            val gameRef = firestore.collection("Games").document(gameId)
            // Obtener el documento de la partida
            val document = gameRef.get().await()
            // Obtener la configuración del juego (numPlayers, initialMoney, etc.)
            val gameConfig = document.toObject(GameConfig::class.java) // Obtener la configuración del juego

            if (gameConfig != null) {
                val maxPlayers = gameConfig.numPlayers // Número máximo de jugadores
                val initialMoney = gameConfig.initialMoney // Dinero inicial por jugador

                // Obtener la referencia a la subcolección "Players"
                val playersRef = gameRef.collection("Players")
                // Contar cuántos jugadores ya están en la partida
                val currentPlayers = playersRef.get().await().size()

                // Verificar si hay espacio para un nuevo jugador
                if (currentPlayers < maxPlayers) {
                    // Crear un nuevo documento en la subcolección "Players" con los datos del jugador
                    val playerData = mapOf(
                        "Name" to playerName,
                        "Money" to initialMoney
                    )
                    playersRef.add(playerData).await()

                    println("Jugador $playerName unido exitosamente a la partida con $initialMoney de dinero inicial.")
                } else {
                    throw Exception("Número máximo de jugadores alcanzado.")
                }
            } else {
                throw Exception("La partida no existe o no se pudo obtener la configuración.")
            }
        } catch (e: Exception) {
            // Manejar errores
            println("Error al unirse a la partida")
            throw e
        }
    }


    // Obtener información de una partida en tiempo real
    fun getGameUpdates(gameId: String, onGameUpdated: (Map<String, Any>) -> Unit) {
        firestore.collection("Games")
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }
                val gameData = snapshot.data ?: return@addSnapshotListener
                onGameUpdated(gameData)
            }
    }

    fun getPlayersUpdates(gameId: String, onGameUpdated: (Map<String, Any>) -> Unit) {
        firestore.collection("Games")
            .document(gameId)
            .collection("Players")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error al escuchar los jugadores conectados: ${error.message}")
                    return@addSnapshotListener
                }
                val playerCount = snapshots?.documents?.size ?: 0
                onGameUpdated(mapOf("numPlayers" to playerCount))
            }
    }

    // Enviar un mensaje al chat de la partida
    suspend fun sendChatMessage(gameId: String, message: String) {
        try {
            val messageData = mapOf(
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("Games")
                .document(gameId)
                .collection("Chat")
                .add(messageData)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    // Escuchar mensajes del chat en tiempo real
    fun getChatMessages(gameId: String, onMessageReceived: (Map<String, Any>) -> Unit) {
        firestore.collection("Games")
            .document(gameId)
            .collection("Chat")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    return@addSnapshotListener
                }
                for (document in snapshots.documentChanges) {
                    if (document.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        onMessageReceived(document.document.data)
                    }
                }
            }
    }

    suspend fun getGameConfig(gameId: String): GameConfig? {
        return try {
            val snapshot = firestore.collection("Games").document(gameId).get().await()
            snapshot.toObject(GameConfig::class.java) // Convierte el documento en un objeto GameConfig
        } catch (e: Exception) {
            println("Error al recuperar la configuración del juego: ${e.message}")
            null
        }
    }
}