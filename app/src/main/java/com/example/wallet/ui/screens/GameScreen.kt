package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wallet.models.GameConfig
import com.example.wallet.services.AuthService
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.Mirage
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.TwilightBlue
import kotlinx.coroutines.launch

@Composable
fun GameScreen(modifier: Modifier = Modifier, gameId: String, navController: NavController) {
    var playerName by remember { mutableStateOf<String?>(null) }
    val authService = AuthService()
    val uid = authService.currentUser?.uid

    LaunchedEffect(gameId, uid) {
        uid?.let {
            val firestoreService = FirestoreService()
            playerName = firestoreService.getPlayerName(gameId, it)
        }
    }

    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa tdo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.SpaceEvenly, // Separar elementos
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {

        GameDetails(gameId = gameId)
        playerName?.let { ActionsGame(navController = navController, gameId = gameId, playerName = it) }
    }
}

@Composable
fun GameDetails(gameId: String) {
    val coroutineScope = rememberCoroutineScope()
    var gameConfig by remember { mutableStateOf<GameConfig?>(null) }
    var playerMoney by remember { mutableStateOf(0) }
    var connectedPlayers by remember { mutableStateOf(0) }

    // Función para recuperar la configuración del juego desde Firestore
    fun fetchGameConfig(gameId: String) {
        coroutineScope.launch {
            try {
                // Recuperar el documento de Firestore usando el gameId
                val firestoreService = FirestoreService()
                val config = firestoreService.getGameConfig(gameId)

                // Asignar los datos recuperados a las variables locales
                gameConfig = config
                playerMoney = config?.initialMoney ?: 0

                println("gameId:  $gameId")
                println("Configuración del juego recuperada: $config")
            } catch (e: Exception) {
                println("Error al recuperar la configuración del juego: ${e.message}")
            }
        }
    }

    // Función para observar los jugadores conectados en tiempo real
    fun observeConnectedPlayers(gameId: String) {
        val firestoreService = FirestoreService()
        firestoreService.getPlayersUpdates(gameId) { gameData ->
            // Actualizar el número de jugadores conectados
            val playerCount = (gameData["numPlayers"] as? Int) ?: 0
            if (connectedPlayers != playerCount) {
                connectedPlayers = playerCount
            }
        }
    }

    // Llamar a la función para recuperar la configuración del juego al inicio
    LaunchedEffect(gameId) {
        fetchGameConfig(gameId)
        observeConnectedPlayers(gameId)
    }

    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sección Game Options
        Text(
            text = "Game Details",
            fontSize = 20.sp,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Bold,
            color = TwilightBlue,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cuadrícula 2x2 para opciones de juego
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameOptionCard(
                    icon = Icons.Default.People,
                    title = "Players",
                    value = "${gameConfig?.numPlayers ?: "N/A"}",
                    modifier = Modifier.weight(1f)
                )
                GameOptionCard(
                    icon = Icons.Default.People,
                    title = "Connected",
                    value = "${connectedPlayers}",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameOptionCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Starting Money",
                    value = "${gameConfig?.initialMoney ?: "N/A"}",
                    modifier = Modifier.weight(1f)
                )
                GameOptionCard(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    title = "Pass Go",
                    value = "${gameConfig?.passGoMoney ?: "N/A"}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionsGame(navController: NavController, gameId: String, playerName: String) {
    // Sección Game Action
    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Game Actions",
            fontSize = 20.sp,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Bold,
            color = TwilightBlue,
            modifier = Modifier.fillMaxWidth()
        )

        GameActionRow(
            icon = Icons.AutoMirrored.Filled.Send,
            text = "Send Money",
            onClick = { /* Navegar a Send Money Screen */ },
            modifier = Modifier.weight(1f)
        )
        GameActionRow(
            icon = Icons.Default.AccountBalance,
            text = "Access Bank",
            onClick = { /* Navegar a Access Bank Screen */ },
            modifier = Modifier.weight(1f)
        )
        GameActionRow(
            icon = Icons.Default.Casino,
            text = "Roll Dice",
            onClick = { navController.navigate("roll_dice/$gameId/$playerName") },
            modifier = Modifier.weight(1f)
        )
        GameActionRow(
            icon = Icons.AutoMirrored.Filled.Chat,
            text = "Live Chat",
            onClick = { navController.navigate("live_chat/$gameId") },
            modifier = Modifier.weight(1f)
        )
    }
}

// Composable para cada card de Game Options
@Composable
fun GameOptionCard(value: String, title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(140.dp)
            .border(1.dp, color= Nepal, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Mirage), // Fondo común
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly // spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = Nepal,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                fontSize = 18.sp,
                color = TwilightBlue
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = Nepal
            )
        }
    }
}

@Composable
fun GameActionRow(icon: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icono con fondo personalizado
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Mirage, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = Nepal,
                modifier = Modifier.size(28.dp)
            )
        }

        // Texto de la acción
        Text(
            text = text,
            fontSize = 18.sp,
            color = TwilightBlue
        )

        // Flecha
        Text(
            text = ">",
            fontSize = 35.sp,
            color = Nepal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    GameScreen(
        gameId = "previewGameId", navController = navController)
}

/*
    //var sendAmount by remember { mutableStateOf("") }


    /*
    // Función para enviar dinero
    fun onSendMoney(amount: Int) {
        // Aquí iría la lógica de enviar dinero
        playerMoney -= amount // Actualiza el dinero localmente
        println("Enviando $amount. Dinero restante: $playerMoney")
    }

    // Función para tirar los dados
    fun onRollDice() {
        val diceResult = (1..6).random() // Simular tirada de dados
        println("Resultado del dado: $diceResult")
        // Aquí puedes manejar el resultado del dado
    } */

    // Mostrar dinero del jugador
    /*Text(
        text = "Dinero disponible: $playerMoney",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    // Mostrar la configuración del juego si ya está cargada
    gameConfig?.let { config ->
        Text(
            text = "Número de jugadores: ${config.numPlayers}",
            fontSize = 16.sp
        )
        Text(
            text = "Dinero inicial: ${config.initialMoney}",
            fontSize = 16.sp
        )
        Text(
            text = "Dinero al pasar GO: ${config.passGoMoney}",
            fontSize = 16.sp
        )
        Text(
            text = "Banco automático: ${if (config.isBankAutomatic) "Sí" else "No"}",
            fontSize = 16.sp
        )
    }

    // Opciones para enviar dinero
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            OutlinedTextField(
                value = sendAmount,
                onValueChange = { newValue ->
                    sendAmount = newValue
                },
                label = { Text("Cantidad a enviar") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                sendAmount.toIntOrNull()?.let { amount -> // Convierte a Int y maneja el caso nulo
                    onSendMoney(amount)
                    sendAmount = "" // Limpia el campo después de enviar
                }
            }) { // Cambia el monto según sea necesario
                Text("Enviar")
            }
        }
    }

    // Botón para tirar los dados
    Button(
        onClick = { onRollDice() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Tirar Dados")
    }*/
*/

/*
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar dinero del jugador
        val playerMoney = 40000
        Text(
            text = "Dinero disponible: $playerMoney",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Opciones para enviar dinero
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = sendAmount,
                onValueChange = { newValue -> sendAmount = newValue },
                label = { Text("Cantidad a enviar") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                sendAmount.toIntOrNull()?.let { amount ->
                    onSendMoney(amount)
                    sendAmount = "" // Limpiar campo después de enviar
                }
            }) {
                Text("Enviar")
            }
        }

        // Botón para tirar los dados
        Button(
            onClick = { onRollDice() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tirar Dados")
        }
    }
*/