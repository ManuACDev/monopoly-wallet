package com.example.wallet.ui.screens.actions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.services.FirestoreService
import com.example.wallet.ui.theme.Mirage
import com.example.wallet.ui.theme.RoyalBlue
import com.example.wallet.ui.theme.TwilightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RollDiceScreen(modifier: Modifier = Modifier, gameId: String, playerName: String, firestoreService: FirestoreService) {
    var diceValue by remember { mutableStateOf(1) }
    var previousDiceValue by remember { mutableStateOf(1) }
    var animationTrigger by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Estado para la rotación final
    var rotationY by remember { mutableStateOf(0f) }

    // Animación de rotación
    val rotationYAnimated by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    // Animación de escala
    val scale by animateFloatAsState(
        targetValue = if (animationTrigger) 1.2f else 1f,
        animationSpec = keyframes {
            durationMillis = 500
            1.2f at 250 // Aumenta hasta 1.2 a mitad de la duración
            1f at 500   // Regresa a 1 al final
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Roll the Dice",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TwilightBlue,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Simulación de cubo
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
                .graphicsLayer(
                    rotationY = rotationYAnimated,
                    scaleX = scale,
                    scaleY = scale
                )
        ) {
            DiceView(diceValue = diceValue, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje y valor del dado con fondo
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(8.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Mensaje dependiendo del valor del dado
                Text(
                    text = when {
                        diceValue == previousDiceValue -> "Volviste a sacar un $diceValue"
                        diceValue == 1 -> "¡Vaya, un uno!"
                        diceValue == 6 -> "¡Increíble, un seis!"
                        else -> "Sacaste un $diceValue"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TwilightBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para lanzar el dado
        Button(
            onClick = {
                previousDiceValue = diceValue // Guarda el valor actual como anterior
                animationTrigger = true
                diceValue = (1..6).random()
                rotationY = if (rotationY == 0f) 180f else 0f // Cambia la rotación para el siguiente lanzamiento

                // Lanza la coroutine para enviar el mensaje al chat
                coroutineScope.launch {
                    val message = "ha lanzado los dados y ha sacado un $diceValue"
                    firestoreService.sendChatMessage(
                        gameId = gameId,
                        playerName = playerName,
                        message = message,
                        type = "dice_roll"
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f)
                .height(50.dp)
        ) {
            Text(
                text = "Roll Dice",
                fontSize = 18.sp
            )
        }

        // Resetea la animación cuando se suelta el botón
        if (animationTrigger) {
            // Reinicia la animación después de 500 ms
            LaunchedEffect(Unit) {
                delay(500)
                animationTrigger = false
            }
        }
    }
}

@Composable
fun DiceView(diceValue: Int, modifier: Modifier = Modifier) {
    // Tamaño del dado
    val diceSize = 100.dp
    val dotSizePx = with(LocalDensity.current) { 12.dp.toPx() }
    val dotColor = Color.Black

    Canvas(
        modifier = modifier
            .size(diceSize)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(2.dp, Mirage, shape = RoundedCornerShape(8.dp))
    ) {
        // Coordenadas de los puntos en el dado (en una matriz de 3x3)
        val positions = listOf(
            Offset(0.25f, 0.25f), // Superior izquierda
            Offset(0.5f, 0.25f),  // Superior centro
            Offset(0.75f, 0.25f), // Superior derecha
            Offset(0.25f, 0.5f),  // Centro izquierda
            Offset(0.5f, 0.5f),   // Centro (medio)
            Offset(0.75f, 0.5f),  // Centro derecha
            Offset(0.25f, 0.75f), // Inferior izquierda
            Offset(0.5f, 0.75f),  // Inferior centro
            Offset(0.75f, 0.75f)  // Inferior derecha
        )

        // Define qué posiciones activar para cada valor del dado
        val activeDots = when (diceValue) {
            1 -> listOf(4)
            2 -> listOf(0, 8)
            3 -> listOf(0, 4, 8)
            4 -> listOf(0, 2, 6, 8)
            5 -> listOf(0, 2, 4, 6, 8)
            6 -> listOf(0, 2, 3, 5, 6, 8)
            else -> emptyList()
        }

        // Dibujar puntos activos en el dado
        activeDots.forEach { index ->
            val position = positions[index]
            drawCircle(
                color = dotColor,
                radius = dotSizePx / 2,
                center = Offset(
                    x = size.width * position.x,
                    y = size.height * position.y
                )
            )
        }
    }
}