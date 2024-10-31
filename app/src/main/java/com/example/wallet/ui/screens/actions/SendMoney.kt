package com.example.wallet.ui.screens.actions

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.models.Player
import com.example.wallet.services.AuthService
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.Mirage
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.PickledBluewood
import com.example.wallet.ui.theme.RoyalBlue
import com.example.wallet.ui.theme.TwilightBlue

@Composable
fun SendMoneyScreen(modifier: Modifier = Modifier, gameId: String) {
    val authService = AuthService()
    val uid = authService.currentUser?.uid

    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa tdo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        if (uid != null) {
            SendDetails(gameId, uid)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun SendDetails(gameId: String, uid: String) {
    // Estado de los jugadores, destinatario y cantidad
    var player by remember { mutableStateOf<Player?>(null) }
    val players = remember { mutableStateListOf<Player>() }
    var amount by remember { mutableStateOf("") }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var expanded by remember { mutableStateOf(false) }
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
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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

        // Card para la transferencia de dinero
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Nepal, shape = RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Mirage),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Transfer Money",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    color = TwilightBlue,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo para ingresar la cantidad
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = {
                        Text("Amount ($)", color = Nepal)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Dolar Icon",
                            tint = Nepal
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Nepal, // Color del texto cuando está enfocado
                        unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                        cursorColor = Nepal, // Color del cursor
                        focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                        unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                        focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                        unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opciones de transferencia
                Text(
                    text = "Transfer To",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    color = TwilightBlue,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TransferOptionCard(
                        icon = Icons.Default.Person,
                        text = "Player",
                        isSelected = transferTo == "Player",
                        onClick = { transferTo = "Player" }
                    )

                    TransferOptionCard(
                        icon = Icons.Default.AccountBalance,
                        text = "Bank",
                        isSelected = transferTo == "Bank",
                        onClick = { transferTo = "Bank" }
                    )

                    TransferOptionCard(
                        icon = Icons.Default.LocalParking,
                        text = "Parking",
                        isSelected = transferTo == "Parking",
                        onClick = { transferTo = "Parking" }
                    )
                }

                // DropdownMenu para seleccionar el destinatario si la opción es Player
                if (transferTo == "Player") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select Player",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = TwilightBlue,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = "Player: ${selectedPlayer?.name}",
                            onValueChange = { /* No es necesario */ },
                            readOnly = true, // No se puede editar el texto
                            placeholder = {
                                Text("Choose player", color = Nepal)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Nepal
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                                cursorColor = Nepal, // Color del cursor
                                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
                            )
                        )

                        // Elemento del menú desplegable
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = true }
                        ) {
                            players.forEach { player ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedPlayer = player // Actualiza el número de jugadores seleccionado
                                        expanded = false // Cierra el menú tras la selección
                                    },
                                    text = { Text(text = player.name) }
                                )
                            }
                        } // Cierra ExposedDropdownMenu
                    } // Cierra ExposedDropdownMenuBox
                }

                // Botón de Transferir
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedPlayer?.let {
                            //val recipientUid = it.uid
                            // Lógica para transferir el dinero
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text(
                        text = "Transfer",
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp
                    )
                }
            }
        } // Cierra Card Transfer

    } // Cierra Column
}

@Composable
fun TransferOptionCard(icon: ImageVector, text: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(80.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RoyalBlue else Nepal,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Mirage),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) RoyalBlue else Nepal
            )

            Text(
                text = text,
                color = if (isSelected) RoyalBlue else Nepal,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
