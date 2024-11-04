@file:Suppress("NAME_SHADOWING")

package com.example.wallet.ui.screens.actions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalParking
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.coroutines.launch

@Composable
fun AccessBankScreen(modifier: Modifier = Modifier, gameId: String) {
    val authService = AuthService()
    val uid = authService.currentUser?.uid

    val scrollState = rememberScrollState()

    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa tdo el tamaño disponible
        .padding(16.dp) // Padding a toda la pantalla
        .verticalScroll(scrollState), // Habilitar scroll vertical
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        if (uid != null) {
            BankActions(gameId, uid)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankActions(gameId: String, uid: String) {
    var player by remember { mutableStateOf<Player?>(null) }
    // Propiedades jugador
    var admin by remember { mutableStateOf(false) }
    var auto by remember { mutableStateOf(false) }
    var banker by remember { mutableStateOf(false) }

    val players = remember { mutableStateListOf<Player>() }

    var amount by remember { mutableStateOf("") }

    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var expandedPlayer by remember { mutableStateOf(false) }

    var selectedBanker by remember { mutableStateOf<Player?>(null) }
    var expandedBanker by remember { mutableStateOf(false) }

    var transferFrom by remember { mutableStateOf("Player") }

    val firestoreService = FirestoreService()
    val coroutineScope = rememberCoroutineScope()

    // Recupera la configuración de la partida, jugadores y al usuario actual
    LaunchedEffect(gameId) {
        val config = firestoreService.getGameConfig(gameId)
        if (config != null) {
            auto = config.isBankAutomatic
        }

        firestoreService.getPlayer(gameId, uid) { updatedPlayer ->
            updatedPlayer?.let { player = it }
            admin = player?.admin == true
            banker = player?.banker == true
        }

        firestoreService.getGamePlayers(gameId) { playerList ->
            players.clear()
            players.addAll(
                playerList.mapNotNull { playerData ->
                    val name = playerData["Name"] as? String
                    val money = playerData["Money"] as? Long
                    val playerUid = playerData["Uid"] as? String
                    val admin = playerData["Admin"] as? Boolean
                    val banker = playerData["Banker"] as? Boolean

                    if (name != null && money != null && playerUid != null && admin != null && banker != null) {
                        Player(name, money, playerUid, admin, banker)
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
        } // Cierra Card Balance

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
                // Aparece si eres admin y la banca es manual
                if (admin == true && auto == false) {
                    Text(
                        text = "Select Banker",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = TwilightBlue,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(expanded = expandedBanker, onExpandedChange = { expandedBanker = !expandedBanker }) {
                        OutlinedTextField(
                            value = "Banker: ${selectedBanker?.name}",
                            onValueChange = { /* No es necesario */ },
                            readOnly = true, // No se puede editar el texto
                            placeholder = {
                                Text("Choose banker", color = Nepal)
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
                            expanded = expandedBanker,
                            onDismissRequest = { expandedBanker = true }
                        ) {
                            players.forEach { player ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedBanker = player // Actualiza el número de jugadores seleccionado
                                        expandedBanker = false // Cierra el menú tras la selección
                                    },
                                    text = { Text(text = player.name) }
                                )
                            }
                        } // Cierra ExposedDropdownMenu
                    } // Cierra ExposedDropdownMenuBox

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            selectedBanker?.let { banker ->
                                coroutineScope.launch {
                                    firestoreService.updatePlayer(
                                        uid = banker.uid,
                                        path = "Games/$gameId/Players", // Ruta a la colección de jugadores
                                        field = "Banker"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = "Assign Banker",
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Opciones de transferencia
                Text(
                    text = "Transfer From",
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
                        icon = Icons.Default.AccountBalance,
                        text = "Bank",
                        isSelected = transferFrom == "Bank",
                        onClick = { transferFrom = "Bank" }
                    )

                    TransferOptionCard(
                        icon = Icons.Default.LocalParking,
                        text = "Parking",
                        isSelected = transferFrom == "Parking",
                        onClick = { transferFrom = "Parking" }
                    )
                }

                // DropdownMenu para seleccionar la cantidad si la opción es Bank y Auto
                // Si es manual, si la opción es Bank y eres el banquero
                if (transferFrom == "Bank" && (auto == true || banker == true)) {
                    Spacer(modifier = Modifier.height(8.dp))

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
                }

                // Aparece si la banca es manual y eres el banquero
                if (auto == false && banker == true) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Select Player",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        color = TwilightBlue,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(expanded = expandedPlayer, onExpandedChange = { expandedPlayer = !expandedPlayer }) {
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
                            expanded = expandedPlayer,
                            onDismissRequest = { expandedPlayer = true }
                        ) {
                            players.forEach { player ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedPlayer = player // Actualiza el número de jugadores seleccionado
                                        expandedPlayer = false // Cierra el menú tras la selección
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
                        val amountToTransfer = amount.toIntOrNull()
                        if (player != null && amountToTransfer != null && amountToTransfer > 0) {
                            coroutineScope.launch {
                                try {
                                    /*firestoreService.transferMoney(
                                        amount = amountToTransfer,
                                        sender = player!!,
                                        gameId = gameId,
                                        transferTo = transferTo,
                                        recipientPlayer = if (transferTo == "Player") selectedPlayer else null
                                    )
                                    val message: String
                                    if (transferTo == "Player") {
                                        message = "ha enviado $amount$ al jugador ${selectedPlayer?.name}"
                                    } else if (transferTo == "Bank") {
                                        message = "ha enviado $amount$ a la Banca"
                                    } else {
                                        message = "ha enviado $amount$ al Parking"
                                    }
                                    firestoreService.sendChatMessage(
                                        gameId = gameId,
                                        playerName = player!!.name,
                                        message = message,
                                        type = "send_money"
                                    )*/
                                } catch (e: Exception) {
                                    println("Error: ${e.message}")
                                }
                            }
                        } else {
                            // Manejar casos de error, como cuando amountToTransfer es nulo o menor o igual a cero
                            println("Error: Ingrese un monto válido para transferir.")
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
    }
}

@Preview(showBackground = true)
@Composable
fun AccessBankScreenPreview() {
    //BankActions()
}
