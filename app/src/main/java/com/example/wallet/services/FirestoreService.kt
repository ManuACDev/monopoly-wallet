package com.example.wallet.services

import com.example.wallet.models.Bank
import com.example.wallet.models.GameConfig
import com.example.wallet.models.Parking
import com.example.wallet.models.Player
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class FirestoreService {

    private val firestore = FirebaseFirestore.getInstance()

    // Crear una nueva partida
    suspend fun createGame(gameId: String, data: Map<String, Any>) {
        try {
            val gameRef = firestore.collection("Games").document(gameId)

            // Crear el documento de la partida
            gameRef.set(data).await()

            // Crear subdocumento "Banca" con dinero infinito
            val bankData = mapOf(
                "Name" to "Banca",
                "Money" to 100000000 // Representa dinero infinito, o usa un valor muy alto si prefieres
            )
            gameRef.collection("Bank").document("Banca").set(bankData).await()

            // Crear subdocumento "Parking" con dinero inicial de 0
            val parkingData = mapOf(
                "Name" to "Parking",
                "Money" to 0
            )
            gameRef.collection("Bank").document("Parking").set(parkingData).await()

            println("Partida $gameId creada exitosamente con subdocumentos Banca y Parking.")
        } catch (e: Exception) {
            // Manejar errores
            println("Error al crear la partida")
            throw e
        }
    }

    // Unirse a una partida
    suspend fun joinGame(gameId: String, playerName: String, userId: String, admin: Boolean, banker: Boolean) {
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
                        "Money" to initialMoney,
                        "Uid" to userId,
                        "Admin" to admin,
                        "Banker" to banker
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


    // Obtener dinero de la banca y parking
    fun getBanK(gameId: String, updatedBank: (Bank?) -> Unit, updatedParking: (Parking?) -> Unit) {
        val bankRef = firestore.collection("Games")
            .document(gameId)
            .collection("Bank")
            .document("Banca")

        val parkingRef = firestore.collection("Games")
            .document(gameId)
            .collection("Bank")
            .document("Parking")

        // Escuchar cambios en Banca en tiempo real
        bankRef.addSnapshotListener { bankDocument, error ->
            if (error != null || bankDocument == null) {
                println("Error al escuchar cambios en la banca: ${error?.message}")
                updatedBank(null)
                return@addSnapshotListener
            }

            val banca = bankDocument.let {
                Bank(
                    name = it.getString("Name") ?: "Banca",
                    money = it.getLong("Money") ?: 0L
                )
            }
            updatedBank(banca)
        }

        // Escuchar cambios en Parking en tiempo real
        parkingRef.addSnapshotListener { parkingDocument, error ->
            if (error != null || parkingDocument == null) {
                println("Error al escuchar cambios en el parking: ${error?.message}")
                updatedParking(null)
                return@addSnapshotListener
            }

            val parking = parkingDocument.let {
                Parking(
                    name = it.getString("Name") ?: "Parking",
                    money = it.getLong("Money") ?: 0L
                )
            }
            updatedParking(parking)
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
    suspend fun sendChatMessage(gameId: String, playerName: String, message: String, type: String) {
        try {
            val messageData = mapOf(
                "playerName" to playerName,
                "message" to message,
                "messageType" to type,
                "timestamp" to FieldValue.serverTimestamp()
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
            .orderBy("timestamp", Query.Direction.ASCENDING) // Ordenar por timestamp
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

    fun getPlayer(gameId: String, uid: String, onPlayerUpdated: (Player?) -> Unit) {
        firestore.collection("Games")
            .document(gameId)
            .collection("Players")
            .whereEqualTo("Uid", uid)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error al escuchar cambios en el jugador: ${error.message}")
                    onPlayerUpdated(null)
                    return@addSnapshotListener
                }

                val playerDocument = snapshots?.documents?.firstOrNull()
                val player = playerDocument?.let { document ->
                    Player(
                        name = document.getString("Name") ?: "Invitado",
                        money = document.getLong("Money") ?: 0L,
                        uid = document.getString("Uid") ?: uid,
                        admin = document.getBoolean("Admin") ?: false,
                        banker = document.getBoolean("Banker") ?: false
                    )
                }
                onPlayerUpdated(player)
            }
    }

    // Actualizar jugador
    suspend fun updatePlayer(uid: String, path: String, field: String) {
        try {
            // Obtener el documento que coincide con el Uid proporcionado
            val querySnapshot = firestore.collection(path)
                .whereEqualTo("Uid", uid)
                .get()
                .await()

            // Si el documento existe, realizar la actualización
            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    // Obtener el valor actual del campo
                    val currentValue = document.getBoolean(field) ?: false
                    // Invertir el valor
                    val newValue = !currentValue

                    // Actualizar el campo con el valor invertido
                    document.reference.update(field, newValue).await()
                    println("Campo $field actualizado a $newValue para el jugador con uid $uid en la colección $path")
                }
            } else {
                println("No se encontró un jugador con el uid $uid en la colección $path")
            }
        } catch (e: Exception) {
            println("Error al actualizar el campo $field: ${e.message}")
        }
    }

    // Recuperar los jugadores
    fun getGamePlayers(gameId: String, onPlayersUpdated: (List<Map<String, Any>>) -> Unit) {
        firestore.collection("Games")
            .document(gameId)
            .collection("Players")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    return@addSnapshotListener
                }
                // Mapea los documentos de los jugadores a una lista de datos
                val playersList = snapshots.documents.mapNotNull { it.data }
                onPlayersUpdated(playersList)
            }
    }

    // Enviar dinero
    suspend fun transferMoney(amount: Int, sender: Player, gameId: String, transferTo: String, recipientPlayer: Player? = null) {
        val gameRef = firestore.collection("Games").document(gameId)

        when (transferTo) {
            "Player" -> {
                if (recipientPlayer != null) {
                    // Obtener documentos de sender y recipient fuera de la transacción
                    val senderSnapshot = gameRef.collection("Players")
                        .whereEqualTo("Uid", sender.uid)
                        .get()
                        .await()
                        .documents
                        .firstOrNull()
                        ?: throw Exception("Error: No se encontró el jugador remitente con UID: ${sender.uid}")

                    val recipientSnapshot = gameRef.collection("Players")
                        .whereEqualTo("Uid", recipientPlayer.uid)
                        .get()
                        .await()
                        .documents
                        .firstOrNull()
                        ?: throw Exception("Error: No se encontró el jugador destinatario con UID: ${recipientPlayer.uid}")

                    val senderRef = senderSnapshot.reference
                    val recipientRef = recipientSnapshot.reference

                    firestore.runTransaction { transaction ->
                        // Leer el saldo del remitente y del destinatario al inicio
                        val senderMoney = transaction.get(senderRef).getLong("Money") ?: 0
                        val recipientMoney = transaction.get(recipientRef).getLong("Money") ?: 0

                        // Actualizar el saldo del remitente
                        transaction.update(senderRef, "Money", senderMoney - amount)

                        // Actualizar el saldo del destinatario
                        transaction.update(recipientRef, "Money", recipientMoney + amount)
                    }.addOnSuccessListener {
                        println("Transferencia a jugador completada con éxito.")
                    }.addOnFailureListener { e ->
                        println("Error en la transferencia: ${e.message}")
                    }
                }
            }

            "Bank" -> {
                // Obtener documentos de sender y bank fuera de la transacción
                val senderSnapshot = gameRef.collection("Players")
                    .whereEqualTo("Uid", sender.uid)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?: throw Exception("Error: No se encontró el jugador remitente con UID: ${sender.uid}")

                val bankRef = gameRef.collection("Bank").document("Banca")

                val senderRef = senderSnapshot.reference

                firestore.runTransaction { transaction ->
                    // Leer el saldo del remitente y del banco al inicio
                    val senderMoney = transaction.get(senderRef).getLong("Money") ?: 0
                    val bankMoney = transaction.get(bankRef).getLong("Money") ?: 0

                    // Actualizar el saldo del remitente
                    transaction.update(senderRef, "Money", senderMoney - amount)

                    // Actualizar el saldo de la banca
                    transaction.update(bankRef, "Money", bankMoney + amount)
                }.addOnSuccessListener {
                    println("Transferencia a banca completada con éxito.")
                }.addOnFailureListener { e ->
                    println("Error en la transferencia: ${e.message}")
                }
            }

            "Parking" -> {
                // Obtener documentos de sender y parking fuera de la transacción
                val senderSnapshot = gameRef.collection("Players")
                    .whereEqualTo("Uid", sender.uid)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?: throw Exception("Error: No se encontró el jugador remitente con UID: ${sender.uid}")

                val parkingRef = gameRef.collection("Bank").document("Parking")

                val senderRef = senderSnapshot.reference

                firestore.runTransaction { transaction ->
                    // Leer el saldo del remitente y del estacionamiento al inicio
                    val senderMoney = transaction.get(senderRef).getLong("Money") ?: 0
                    val parkingMoney = transaction.get(parkingRef).getLong("Money") ?: 0

                    // Actualizar el saldo del remitente
                    transaction.update(senderRef, "Money", senderMoney - amount)

                    // Actualizar el saldo del parking
                    transaction.update(parkingRef, "Money", parkingMoney + amount)
                }.addOnSuccessListener {
                    println("Transferencia a parking completada con éxito.")
                }.addOnFailureListener { e ->
                    println("Error en la transferencia: ${e.message}")
                }
            }
        }
    }
}