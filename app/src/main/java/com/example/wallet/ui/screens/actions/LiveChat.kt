package com.example.wallet.ui.screens.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallet.models.ChatMessage
import com.example.wallet.services.FirestoreService

@Composable
fun LiveChatScreen(modifier: Modifier = Modifier, gameId: String) {
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Observa los mensajes en tiempo real
    LaunchedEffect(gameId) {
        val firestoreService = FirestoreService()
        firestoreService.getChatMessages(gameId) { data ->
            // Recupera el nombre del jugador de los datos
            val sender = data["playerName"] as? String ?: "Desconocido"
            val content = data["message"] as? String ?: ""
            if (content.isNotEmpty()) {
                messages.add(ChatMessage(sender, content)) // Añade el mensaje a la lista
            }
        }
    }

    // UI de la pantalla de chat
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Lista de mensajes
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = message.name, fontWeight = FontWeight.Bold)
            Text(text = message.content)
        }
    }
}