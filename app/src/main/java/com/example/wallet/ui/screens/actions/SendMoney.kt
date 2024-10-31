package com.example.wallet.ui.screens.actions

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.models.Player
import com.example.wallet.services.AuthService
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.Mirage
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.TwilightBlue

@Composable
fun SendMoneyScreen(modifier: Modifier = Modifier, gameId: String) {
    val authService = AuthService()
    val uid = authService.currentUser?.uid

    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa tdo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.SpaceEvenly, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        if (uid != null) {
            SendDetails(gameId, uid)
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun SendDetails(gameId: String, uid: String) {
    // Estado de los jugadores, destinatario y cantidad
    var player by remember { mutableStateOf<Player?>(null) }
    val players = remember { mutableStateListOf<Player>() }
    var amount by remember { mutableStateOf("") }
    var selectedRecipient by remember { mutableStateOf<Player?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var transferTo by remember { mutableStateOf("Player") }

    // Recupera jugadores de la partida y excluye al usuario actual
    LaunchedEffect(gameId) {
        val firestoreService = FirestoreService()
        player = firestoreService.getPlayer(gameId, uid)
        firestoreService.getGamePlayers(gameId) { playerList ->
            players.clear()
            players.addAll(
                playerList.mapNotNull { playerData ->
                    val name = playerData["Name"] as? String
                    val money = playerData["Money"] as? Number
                    val playerUid = playerData["Uid"] as? String

                    // Excluye al usuario actual y asegura que todos los campos no sean nulos
                    if (name != null && money != null && playerUid != null && playerUid != uid) {
                        Player(name, money, playerUid)
                    } else {
                        null
                    }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .border(1.dp, color = Nepal, shape = RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Mirage),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Balance",
                    fontSize = 26.sp,
                    color = TwilightBlue,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${player?.money} $",
                    fontSize = 23.sp,
                    color = Nepal
                )
            }
        }


    } // Cierra Column

    Spacer(modifier = Modifier.height(16.dp))

    // Card para la transferencia de dinero
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Transfer Money", style = MaterialTheme.typography.titleMedium)

            // Campo para ingresar la cantidad
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            // Opciones de transferencia
            Text(text = "Transfer To")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = transferTo == "Player",
                    onClick = { transferTo = "Player" }
                )
                Text("Player")
                RadioButton(
                    selected = transferTo == "Bank",
                    onClick = { transferTo = "Bank" }
                )
                Text("Bank")
                RadioButton(
                    selected = transferTo == "Parking",
                    onClick = { transferTo = "Parking" }
                )
                Text("Parking")
            }

            // DropdownMenu para seleccionar el destinatario si la opción es Player
            if (transferTo == "Player") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Select Player")

                Button(
                    onClick = { isDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = selectedRecipient?.name ?: "Select Recipient")
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    players.forEach { player ->
                        DropdownMenuItem(
                            text = { Text(text = player.name) },
                            onClick = {
                                selectedRecipient = player
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Botón de Transferir
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    selectedRecipient?.let {
                        //val recipientUid = it.uid
                        // Lógica para transferir el dinero
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Transfer")
            }
        }
    }
}
