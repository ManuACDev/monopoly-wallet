package com.example.wallet.ui.screens

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wallet.R
import com.example.wallet.models.GameConfig
import com.example.wallet.models.Player
import com.example.wallet.services.AuthService
import com.example.wallet.services.FirestoreService
import com.example.wallet.services.InteractionService
import com.example.wallet.ui.components.NativeAdComponent
import com.example.wallet.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val scrollState = rememberScrollState()
    val authService = AuthService()
    val firestoreService = FirestoreService()

    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa tdo el tamaño disponible
        .padding(16.dp) // Padding a toda la pantalla
        .verticalScroll(scrollState), // Habilitar scroll vertical
        verticalArrangement = Arrangement.SpaceEvenly, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        CustomScreen(navController = navController)
        JoinScreen(navController = navController)
        NativeAdComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // Padding para separar el anuncio de los otros elementos
        )
        GamesList(navController = navController, authService = authService, firestoreService = firestoreService)
        NativeAdComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // Padding para separar el anuncio de los otros elementos
        )
    }
}

@Composable
fun CustomScreen(navController: NavController) {
    val interactionService = InteractionService(LocalContext.current)

    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen redondeada que ocupa tdo el ancho posible
        Image(
            painter = painterResource(id = R.drawable.juegomesa), // Ruta de la imagen local
            contentDescription = "Game image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(14.dp))
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Columna para título y subtítulo alineados a la izquierda
        Column(
            modifier = Modifier.fillMaxWidth(), // Ocupa tdo el ancho
            horizontalAlignment = Alignment.Start // Alinea a la izquierda
        ) {
            // Título
            Text(
                text = "Create New Game",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TwilightBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtítulo
            Text(
                text = "Start a new game",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = CadetBlue
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mensaje de invitación centrado
        Text(
            text = "Invite friends to join this game with the code",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = AthensGray,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de "Start Game" centrado
        Button(
            onClick = {
                interactionService.showToast("Charging...", Toast.LENGTH_SHORT)
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        navController.navigate("game_options")
                    } catch (e: Exception) {
                        println("Error al navegar: ${e.message}")
                        interactionService.showToast("Error at navigating", Toast.LENGTH_SHORT)
                    }
                }, 1000L)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
        ) {
            Text(
                text = "Start Game",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun JoinScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var showJoinDialog by remember { mutableStateOf(false) }
    val firestoreService = FirestoreService()
    val authService = AuthService()
    val interactionService = InteractionService(LocalContext.current)
    var isButtonEnabled by remember { mutableStateOf(true) }
    var isGameJoined by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ajusta las alturas entre columnas
        ) {
            // Parte izquierda con título, subtítulo y chip
            Column(
                modifier = Modifier
                    .weight(1f) // Toma tdo el espacio restante
                    .align(Alignment.CenterVertically) // Alinea verticalmente al centro
                    .clickable { showJoinDialog = true } // Abrir el diálogo al hacer clic en cualquier parte de la columna
            ) {
                Text(
                    text = "Join a Game",
                    color = TwilightBlue
                )

                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre elementos

                Text(
                    text = "Enter the game code",
                    color = CadetBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chip simulado con un contenedor redondeado
                Box(
                    modifier = Modifier
                        .background(PickledBluewood, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Join a Game",
                        color = Color.White
                    )
                }

                // Mostrar el diálogo para unirse a una partida
                if (showJoinDialog) {
                    JoinGameDialog(
                        onDismiss = { showJoinDialog = false },
                        onJoinGame = onJoinGame@ { gameId, playerName ->
                            if (isGameJoined) return@onJoinGame
                            interactionService.showToast("Joining the game...", Toast.LENGTH_LONG)
                            isButtonEnabled = false
                            isGameJoined = true

                            Handler(Looper.getMainLooper()).postDelayed({
                                coroutineScope.launch {
                                    try {
                                        val userId = authService.currentUser?.uid
                                        if (userId != null) {
                                            firestoreService.joinGame(gameId, playerName, userId, false, false) // Usar playerName
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("gameScreen/$gameId") { // Navegar si es exitoso
                                                    popUpTo("home") { inclusive = true } // Elimina HomeScreen de la pila
                                                }
                                            }
                                        } else {
                                            println("Error: error al unirse a la partida.")
                                            interactionService.showToast("Error joining the game.", Toast.LENGTH_SHORT)
                                            isButtonEnabled = true
                                            isGameJoined = false
                                        }
                                    } catch (e: Exception) {
                                        println("Error al unirse a la partida: ${e.message}")
                                        interactionService.showToast(e.message ?: "Error joining the game", Toast.LENGTH_SHORT)
                                        isButtonEnabled = true
                                        isGameJoined = false
                                    }
                                }
                            }, 1000L)
                        },
                        isButtonEnabled = isButtonEnabled
                    )
                }
            }

            // Parte derecha con la imagen
            Image(
                painter = painterResource(id = R.drawable.joingame),
                contentDescription = "Game Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(118.dp) // Tamaño de la imagen
                    .clip(RoundedCornerShape(14.dp))
                    .align(Alignment.CenterVertically) // Alinear verticalmente al centro
            )
        }
    }
}

@Composable
fun JoinGameDialog(onDismiss: () -> Unit, onJoinGame: (String, String) -> Unit, isButtonEnabled: Boolean) {
    var gameId by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = Vulcan,
        title = {
            Text(
                text = "Join a game",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TwilightBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Añadir padding para el contenido
                horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
            ) {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    placeholder = {
                        Text("Player Name", color = Nepal) // Placeholder en color blanco
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
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

                Spacer(modifier = Modifier.height(20.dp)) // Espacio entre campos

                OutlinedTextField(
                    value = gameId,
                    onValueChange = { gameId = it },
                    placeholder = {
                        Text("Game Code", color = Nepal) // Placeholder en color blanco
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
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
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isButtonEnabled) {
                        if (gameId.isNotEmpty() && playerName.isNotEmpty()) {
                            try {
                                onJoinGame(gameId, playerName) // Pasar tanto el gameId como el playerName
                            } catch (e: Exception) {
                                println("Error al unirse al juego: ${e.message}")
                            }
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
                    text = "Join",
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun GamesList(navController: NavController, authService: AuthService, firestoreService: FirestoreService) {
    val userId = authService.currentUser?.uid
    var userGames by remember { mutableStateOf<List<Pair<GameConfig, Player?>>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            userGames = firestoreService.getPlayerGamesConfig(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), // Ocupa tdo el ancho
            horizontalAlignment = Alignment.Start // Alinea a la izquierda
        ) {
            Text(
                text = "Your Games",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TwilightBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Join a game again",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = CadetBlue
            )

            if (userGames.isEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "No games found",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AthensGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Column para mostrar la lista de juegos
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            userGames.forEach { (game, player) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, color = PickledBluewood, shape = RoundedCornerShape(12.dp))
                        .clickable {
                            // Usamos el gameId para la navegación
                            navController.navigate("gameScreen/${game.gameId}") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Mirage),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Sección superior con Game Code y número de jugadores
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Game Code: ${game.gameId}",
                                fontSize = 20.sp,
                                color = TwilightBlue,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Person Icon",
                                    tint = Nepal,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "${game.numPlayers} players",
                                    fontSize = 16.sp,
                                    color = TwilightBlue,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        HorizontalDivider(thickness = 2.dp, color = PickledBluewood)

                        // Sección de información del jugador
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Player Name:",
                                    fontSize = 14.sp,
                                    color = CadetBlue,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = player?.name ?: "Unknown",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AthensGray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Money:",
                                    fontSize = 14.sp,
                                    color = CadetBlue,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "$${player?.money ?: 0}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AthensGray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}