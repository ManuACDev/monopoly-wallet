package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.models.GameConfig
import com.example.wallet.services.FirestoreService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(gameId: String) {
    val coroutineScope = rememberCoroutineScope()
    var gameConfig by remember { mutableStateOf<GameConfig?>(null) }
    var playerMoney by remember { mutableStateOf(0) }
    var sendAmount by remember { mutableStateOf("") }

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

    // Llamar a la función para recuperar la configuración del juego al inicio
    LaunchedEffect(gameId) {
        fetchGameConfig(gameId)
    }

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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pantalla de Partida")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar dinero del jugador
            Text(
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
            }

            // Aquí puedes agregar más opciones o información sobre la partida
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameScreen(
        gameId = "previewGameId"
    )
}