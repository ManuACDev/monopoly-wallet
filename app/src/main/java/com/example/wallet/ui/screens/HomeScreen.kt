package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Crear la pantalla con un layout vertical
    Column(modifier = Modifier
        .fillMaxSize() // La pantalla ocupa todo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        CustomCard()
    }
}

@Composable
fun CustomCard() {
    // Crear la tarjeta
    Card(modifier = Modifier
        .fillMaxWidth() // La tarjeta ocupará todo el ancho
        .height(125.dp) // Define un alto específico para la tarjeta
        .padding(8.dp), // Padding para que la tarjeta no esté pegada a los bordes
        elevation = CardDefaults.cardElevation(4.dp), // Añadir una pequeña sombra
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.5f)), // Fondo gris claro con transparencia
        shape = RoundedCornerShape(8.dp) // Esquinas redondeadas
    ) {
        // Crear una fila (Row) para colocar el "+" y el texto en la misma línea
        Row(modifier = Modifier
            .fillMaxSize() // Llenar el tamaño de la tarjeta
            .padding(16.dp), // Padding interno de la tarjeta
            verticalAlignment = Alignment.CenterVertically, // Alinear verticalmente el contenido
            horizontalArrangement = Arrangement.Center // Centrar horizontalmente
        ) {
            // Crear el símbolo "+" con fondo redondo y gris oscuro
            Box(modifier = Modifier
                .size(50.dp) // Tamaño del círculo
                .background(color = Color(0xFFD0D0D0), shape = CircleShape), // Fondo gris claro y redondo
                contentAlignment = Alignment.Center // Centrar el contenido dentro del círculo
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp, // Tamaño del texto más grande
                    color = Color.White // Color del texto blanco
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Separar el "+" del texto

            // Texto para "Partida personalizada" con estilo más grande y atractivo
            Text(
                text = "PARTIDA PERSONALIZADA",
                fontSize = 18.sp, // Tamaño de la fuente más grande
                fontWeight = FontWeight.Bold, // Texto en negrita
                color = Color.Black // Color del texto negro
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}