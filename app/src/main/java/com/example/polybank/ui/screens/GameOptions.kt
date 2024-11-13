package com.example.polybank.ui.screens

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.polybank.models.GameConfig
import com.example.polybank.services.AuthService
import com.example.polybank.services.FirestoreService
import com.example.polybank.services.InteractionService
import com.example.polybank.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun GameOptions(modifier: Modifier = Modifier, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var isButtonEnabled by remember { mutableStateOf(true) }
    var isGameCreated by remember { mutableStateOf(false) }
    val interactionService = InteractionService(LocalContext.current)

    Column(
        modifier = modifier
            .fillMaxSize() // La pantalla ocupa el tamaño disponible
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        // Aquí pasamos un valor a `onGameCreated` y `onBack`
        GameContent(
            onGameCreated = onGameCreated@ { config, creatorName ->
                if (isGameCreated) return@onGameCreated
                interactionService.showToast("Creating the game...", Toast.LENGTH_LONG)
                isButtonEnabled = false
                isGameCreated = true

                Handler(Looper.getMainLooper()).postDelayed({
                    coroutineScope.launch {
                        try {
                            val gameId = createGameInFirestore(config, creatorName) // Llamar a la función suspend
                            println("Partida creada con la configuración: $config y creador: $creatorName")

                            if (gameId != null) {
                                // Navegar a GameScreen pasando solo el gameId
                                navController.navigate("gameScreen/$gameId") {
                                    popUpTo("game_options") { inclusive = true } // Elimina la pantalla anterior
                                }
                            } else {
                                println("Error: el ID de la partida es inválido.")
                                interactionService.showToast("Error creating the game.", Toast.LENGTH_SHORT)
                                isButtonEnabled = true
                                isGameCreated = false
                            }
                        } catch (e: Exception) {
                            println("Error al crear la partida: ${e.message}")
                            interactionService.showToast("Error creating the game", Toast.LENGTH_SHORT)
                            isButtonEnabled = true
                            isGameCreated = false
                        }
                    }
                }, 1000L)
            },
            isButtonEnabled = isButtonEnabled
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameContent(onGameCreated: (GameConfig, String) -> Unit, isButtonEnabled: Boolean) {
    var numPlayers by remember { mutableStateOf(2) }
    var initialMoney by remember { mutableStateOf("300000") }
    var passGoMoney by remember { mutableStateOf("40000") }
    var isBankAutomatic by remember { mutableStateOf(false) }
    var creatorName by remember { mutableStateOf("") }

    val buttonEnabled = numPlayers >= 2 &&
            initialMoney.isNotEmpty() &&
            passGoMoney.isNotEmpty() &&
            creatorName.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        item {
            Text(
                text = "Game Setup",
                fontSize = 20.sp,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold,
                color = TwilightBlue,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Nombre del creador de la partida
        item {
            OutlinedTextField(
                value = creatorName,
                onValueChange = { creatorName = it },
                placeholder = {
                    Text("Your Name", color = Nepal)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person, // Icono de persona
                        contentDescription = "Person Icon",
                        tint = Nepal // Cambia el color del ícono si es necesario
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

        // Número de jugadores
        item {
            var expanded by remember { mutableStateOf(false) }
            val numPlayersOptions = (2..6).toList() // Opciones de número de jugadores disponibles

            // Crear el menú desplegable
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = "Max players: $numPlayers",
                    onValueChange = { /* No es necesario */ },
                    readOnly = true, // No se puede editar el texto
                    placeholder = {
                        Text("Max Players", color = Nepal)
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
                    numPlayersOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                numPlayers = option // Actualiza el número de jugadores seleccionado
                                expanded = false // Cierra el menú tras la selección
                            },
                            text = { Text("$option Players") }
                        )
                    }
                }
            }
        }

        // Dinero inicial
        item {
            OutlinedTextField(
                value = initialMoney,
                onValueChange = { initialMoney = it },
                placeholder = {
                    Text("Initial Money", color = Nepal)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney, // Icono de dinero
                        contentDescription = "Money Icon",
                        tint = Nepal // Cambia el color del ícono si es necesario
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

        // Dinero por pasar la salida
        item {
            OutlinedTextField(
                value = passGoMoney,
                onValueChange = { passGoMoney = it },
                placeholder = {
                    Text("Pass Go", color = Nepal)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney, // Icono de dinero
                        contentDescription = "Money Icon",
                        tint = Nepal // Cambia el color del ícono si es necesario
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

        // Banca automática
        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp), // Espacio entre los elementos dentro del item
            ) {
                // CheckBox para Banca Automática
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa tdo el ancho disponible
                        .border(2.dp, PickledBluewood, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Separa el texto del CheckBox
                ) {
                    Text(
                        text = "Automated Bank",
                        color = Nepal,
                        modifier = Modifier.padding(10.dp)
                    )
                    Checkbox(
                        checked = isBankAutomatic,
                        onCheckedChange = {
                            isBankAutomatic = true // Marcar banca automática
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PickledBluewood, // Fondo cuando está seleccionado
                            uncheckedColor = Nepal, // Borde cuando está desmarcado
                            checkmarkColor = Nepal, // Color de la marca ✓ cuando está seleccionado
                            disabledCheckedColor = TwilightBlue, // Fondo cuando está deshabilitado y marcado
                            disabledUncheckedColor = TwilightBlue // Borde cuando está deshabilitado y desmarcado
                        )
                    )
                }

                // CheckBox para Banca Manual
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa tdo el ancho disponible
                        .border(2.dp, PickledBluewood, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Separa el texto del CheckBox
                ) {
                    Text(
                        text = "Manual Bank",
                        color = Nepal,
                        modifier = Modifier.padding(10.dp)
                    )
                    Checkbox(
                        checked = !isBankAutomatic, // Si no es automático, es manual
                        onCheckedChange = {
                            isBankAutomatic = false // Marcar banca manual
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PickledBluewood, // Fondo cuando está seleccionado
                            uncheckedColor = Nepal, // Borde cuando está desmarcado
                            checkmarkColor = Nepal, // Color de la marca ✓ cuando está seleccionado
                            disabledCheckedColor = TwilightBlue, // Fondo cuando está deshabilitado y marcado
                            disabledUncheckedColor = TwilightBlue // Borde cuando está deshabilitado y desmarcado
                        )
                    )
                }
            }
        }

        // Botón para crear partida
        item {
            Button(
                onClick = {
                    // Solo ejecuta la creación de partida si todas las condiciones están cumplidas
                    if (buttonEnabled) {
                        try {
                            val gameConfig = GameConfig(
                                gameId = "docId",
                                numPlayers = numPlayers,
                                initialMoney = initialMoney.toInt(),
                                passGoMoney = passGoMoney.toInt(),
                                isBankAutomatic = isBankAutomatic
                            )
                            onGameCreated(gameConfig, creatorName) // Configuración de la partida y el nombre del creador
                        } catch (e: Exception) {
                            println("Error al crear el juego: ${e.message}")
                        }
                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue,
                    disabledContainerColor = PickledBluewood
                )
            ) {
                Text(
                    text = "Create Game",
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        }
    } // Cierra LazyColumn
}

suspend fun createGameInFirestore(config: GameConfig, creatorName: String): String? {
    val firestoreService = FirestoreService()
    val authService = AuthService()

    // Generar un ID único para la partida
    val gameId = UUID.randomUUID().toString().take(8)

    // Crear el mapa de datos para la partida
    val data = mapOf(
        "gameId" to gameId,
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
        val userId = authService.currentUser?.uid
        if (userId != null) {
            firestoreService.joinGame(gameId, creatorName, userId, true, false)
            println("Partida guardada exitosamente en Firestore")
            return gameId
        } else {
            println("No se pudo obtener el ID de usuario. El jugador no puede unirse a la partida.")
            return null
        }
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
        isButtonEnabled = false
    )
}
