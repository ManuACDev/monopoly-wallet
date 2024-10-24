package com.example.wallet.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wallet.models.GameConfig
import com.example.wallet.services.FirestoreService
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun GameOptions(modifier: Modifier = Modifier, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize(), // La pantalla ocupa el tamaño disponible
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        // Aquí pasamos un valor a `onGameCreated` y `onBack`
        GameContent(
            onGameCreated = { config, creatorName ->
                try {
                    coroutineScope.launch {
                        val gameId = createGameInFirestore(config, creatorName) // Llamar a la función suspend
                        println("Partida creada con la configuración: $config y creador: $creatorName")

                        if (gameId.isNotEmpty()) {
                            // Navegar a GameScreen pasando solo el gameId
                            navController.navigate("gameScreen/$gameId")
                        } else {
                            println("Error: el ID de la partida es inválido.")
                        }
                    }
                } catch (e: Exception) {
                    println("Error al crear la partida: ${e.message}")
                }
            },
            onBack = { navController.popBackStack() } // Volver atrás usando popBackStack
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameContent(onGameCreated: (GameConfig, String) -> Unit, onBack: () -> Unit) {
    var numPlayers by remember { mutableStateOf(2) }
    var initialMoney by remember { mutableStateOf("300000") }
    var passGoMoney by remember { mutableStateOf("40000") }
    var isBankAutomatic by remember { mutableStateOf(false) }
    var creatorName by remember { mutableStateOf("") }

    val isCreateButtonEnabled = numPlayers >= 2 &&
            initialMoney.isNotEmpty() &&
            passGoMoney.isNotEmpty() &&
            creatorName.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Partida") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            item {
                Text(
                    text = "Configuración de la Partida",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Nombre del creador de la partida
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Nombre del Creador")
                    OutlinedTextField(
                        value = creatorName,
                        onValueChange = { creatorName = it },
                        label = { Text("Introduce tu nombre") },
                        modifier = Modifier.fillMaxWidth(0.70f)
                    )
                }
            }

            // Número de jugadores
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Número de Jugadores")
                    FlowRow(
                        verticalArrangement = Arrangement.SpaceBetween, // Espacio vertical entre filas
                        horizontalArrangement = Arrangement.Center, // Centramos los botones horizontalmente
                        maxItemsInEachRow = 3 // Mostrar 3 botones por fila
                    ) {
                        for (i in 2..6) {
                            Button(
                                modifier = Modifier.padding(3.dp),
                                onClick = { numPlayers = i },
                                colors = if (numPlayers == i) ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                ) else ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "$i")
                            }
                        }
                    }
                }
            }

            // Dinero inicial
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Dinero Inicial")
                    OutlinedTextField(
                        value = initialMoney,
                        onValueChange = { initialMoney = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(0.70f)
                    )
                }
            }

            // Dinero por pasar la salida
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Dinero por Pasar la Salida")
                    OutlinedTextField(
                        value = passGoMoney,
                        onValueChange = { passGoMoney = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(0.70f)
                    )
                }
            }

            // Banca automática
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Text(text = "Banca Automática")
                        Switch(
                            checked = isBankAutomatic,
                            onCheckedChange = { isBankAutomatic = it })
                    }

                    /*if (!isBankAutomatic) {
                        // Seleccionar banquero
                        var expanded by remember { mutableStateOf(false) }

                        // Crear el menú desplegable
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = "Jugador $selectedBanker", // Muestra el jugador seleccionado
                                onValueChange = { /* No es necesario */ },
                                readOnly = true, // No se puede editar el texto
                                label = { Text("Seleccionar Banquero") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.70f)
                                    .menuAnchor() // Asegura que el menú se alinee correctamente
                            )

                            // Elemento del menú desplegable
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                for (i in 1..numPlayers) {
                                    DropdownMenuItem(onClick = {
                                            selectedBanker = i // Selecciona el jugador como banquero
                                            expanded = false // Cierra el menú después de seleccionar
                                        }, text = { Text("Jugador $i") } // Usando text como un parámetro
                                    )
                                }
                            }
                        }
                    }*/
                }
            }

            // Lista de jugadores conectados
            /*item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Jugadores Conectados", fontWeight = FontWeight.Bold)
                    FlowRow(
                        verticalArrangement = Arrangement.Center, // Centrar elementos verticalmente
                        horizontalArrangement = Arrangement.spacedBy(15.dp), // Espacio horizontal entre filas
                        maxItemsInEachRow = 3 // Mostrar 3 botones por fila
                    ) {
                        playersConnected.forEach { player ->
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = player
                            )
                        }
                    }
                }
            }*/

            // Botón para crear partida
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val gameConfig = GameConfig(
                            numPlayers = numPlayers,
                            initialMoney = initialMoney.toInt(),
                            passGoMoney = passGoMoney.toInt(),
                            isBankAutomatic = isBankAutomatic
                        )
                        onGameCreated(gameConfig, creatorName) // Configuración de la partida y el nombre del creador
                    },
                    enabled = isCreateButtonEnabled
                ) {
                    Text("Crear Partida")
                }
            }
        } // Cierra LazyColumn
    }
}

suspend fun createGameInFirestore(config: GameConfig, creatorName: String): String {
    val firestoreService = FirestoreService()

    // Generar un ID único para la partida
    val gameId = UUID.randomUUID().toString().take(8)

    // Crear el mapa de datos para la partida
    val data = mapOf(
        "numPlayers" to config.numPlayers,
        "initialMoney" to config.initialMoney,
        "passGoMoney" to config.passGoMoney,
        "isBankAutomatic" to config.isBankAutomatic
    )

    // Llamar al servicio de Firestore para crear la partida
    // Se recomienda usar coroutines para manejar el trabajo en segundo plano
    try {
        firestoreService.createGame(gameId, data)
        // Añadir al creador como primer jugador
        firestoreService.joinGame(gameId, creatorName)
        println("Partida guardada exitosamente en Firestore")
        return gameId
    } catch (e: Exception) {
        println("Error al guardar la partida: ${e.message}")
        throw e
    }
}


@Preview(showBackground = true)
@Composable
fun GameContentPreview() {
    GameContent(
        onGameCreated = { gameConfig, creatorName ->
            // Acción simulada al crear la partida
            println("Game created with config: $gameConfig and creator: $creatorName")
        },
        onBack = {
            // Acción simulada al volver
            println("Back button pressed")
        }
    )
}

/*
// Preview para ver el diseño en Compose Preview
fun GameContentPreview() {
    GameContent(
        onGameCreated = { gameConfig ->
            // Aquí puedes realizar acciones como navegar a la pantalla de juego o iniciar la partida
            println("Partida creada con la siguiente configuración: $gameConfig")
        },
        onBack = { /* Acción al volver */ }
    )
}*/
