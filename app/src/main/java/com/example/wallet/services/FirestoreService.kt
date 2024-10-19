package com.example.wallet.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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
    suspend fun joinGame(gameId: String, playerId: String) {
        try {
            val gameRef = firestore.collection("Games").document(gameId)
            gameRef.update("Players", FieldValue.arrayUnion(playerId)).await()
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
}