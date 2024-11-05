package com.example.wallet.ui.screens.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.wallet.models.ChatMessage
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.Mirage
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.PickledBluewood

@Composable
fun LiveChatScreen(modifier: Modifier = Modifier, gameId: String) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    // Observa los mensajes en tiempo real
    LaunchedEffect(gameId) {
        val firestoreService = FirestoreService()
        firestoreService.getChatMessages(gameId) { data ->
            // Recupera el nombre del jugador de los datos
            val sender = data["playerName"] as? String ?: "Desconocido"
            val content = data["message"] as? String ?: ""
            val type = data["messageType"] as? String ?: "send"
            messages.add(ChatMessage(sender, content, type)) // Añade el mensaje a la lista
        }
    }

    // Desplaza al último mensaje cuando la lista de mensajes cambia
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // UI de la pantalla de chat
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
                .background(PickledBluewood, RoundedCornerShape(14.dp))
                .border(1.dp, Nepal, RoundedCornerShape(14.dp)) // Borde del contenedor
        ) {
            // Lista de mensajes
            LazyColumn(
                state = listState, // Usar el estado de la lista para controlar el scroll
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message)
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Obtener el icono según el tipo de mensaje
        val messageIcon = getMessageIcon(message.type)

        // Icono de usuario
        Icon(
            imageVector = messageIcon,
            contentDescription = "Message Icon", // Descripción para accesibilidad
            modifier = Modifier
                .background(color = Mirage, shape = RoundedCornerShape(8.dp))
                .padding(6.dp)
                .size(24.dp),
            tint = Nepal
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Burbuja de mensaje
        Box(
            modifier = Modifier
                .background(Color(0xFFECEFF1), RoundedCornerShape(12.dp)) // Color de fondo de la burbuja
                .padding(8.dp)
                .weight(1f) // Para que la burbuja ocupe el espacio restante
        ) {
            // Composición del mensaje con el nombre en negrita
            val messageText = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${message.name} ")
                }
                append(message.content) // Agregar el contenido del mensaje
            }

            Text(
                text = messageText,
                color = Color.DarkGray // Color del contenido
            )
        }
    }
}

@Composable
fun getMessageIcon(type: String): ImageVector {
    return when (type) {
        "dice_roll" -> Icons.Default.Casino
        "send_money" -> Icons.Default.AttachMoney
        "acs_bank" -> Icons.Default.AccountBalance
        "acs_park" -> Icons.Default.LocalParking
        else -> Icons.AutoMirrored.Filled.Send
    }
}
