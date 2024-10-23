package com.example.wallet.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa todo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        CustomScreen(navController = navController)
        //JoinCard(navController = navController)
    }
}

@Composable
fun JoinCard(navController: NavController) {
    var showJoinDialog by remember { mutableStateOf(false) }
    var showToastMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current // Obtener el contexto fuera de composable
    val firestoreService = FirestoreService()

    showToastMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            showToastMessage = null // Resetear el mensaje después de mostrar el Toast
        }
    }

    // Crear la tarjeta
    Card(modifier = Modifier
        .fillMaxWidth() // La tarjeta ocupará tdo el ancho
        .height(125.dp) // Define un alto específico para la tarjeta
        .padding(8.dp) // Padding para que la tarjeta no esté pegada a los bordes
        .clickable { showJoinDialog = true }, // Navegar a la pantalla de opciones de juego
        elevation = CardDefaults.cardElevation(4.dp), // Añadir una pequeña sombra
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f)), // Fondo gris claro con transparencia
        shape = RoundedCornerShape(8.dp) // Esquinas redondeadas
    ) {
        // Crear una fila (Row) para colocar el "+" y el texto en la misma línea
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unirse a una Partida",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Introduce el enlace para unirte a una partida existente",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Mostrar el diálogo para unirse a una partida
            if (showJoinDialog) {
                JoinGameDialog(
                    onDismiss = { showJoinDialog = false },
                    onJoinGame = { gameId, playerName ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                firestoreService.joinGame(gameId, playerName) // Usar playerName
                                withContext(Dispatchers.Main) {
                                    navController.navigate("gameScreen/$gameId") // Navegar si es exitoso
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    showToastMessage = e.message ?: "Error al unirse a la partida"
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun JoinGameDialog(onDismiss: () -> Unit, onJoinGame: (String, String) -> Unit) {
    var gameId by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Unirse a una Partida")
        },
        text = {
            Column {
                Text("Introduce el enlace o código de la partida:")
                OutlinedTextField(
                    value = gameId,
                    onValueChange = { gameId = it },
                    label = { Text("Enlace o Código") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los campos
                Text("Introduce tu nombre:")
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (gameId.isNotEmpty() && playerName.isNotEmpty()) {
                        onJoinGame(gameId, playerName) // Pasar tanto el gameId como el playerName
                    }
                }
            ) {
                Text("Unirse")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CustomScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                navController.navigate("game_options")
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}