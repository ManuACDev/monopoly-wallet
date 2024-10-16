package com.example.wallet.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Crear la pantalla con un layout vertical
    Column(modifier = Modifier
        .fillMaxSize() // La pantalla ocupa todo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.Center, // Centra los elementos verticalmente
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
        .padding(8.dp), // Padding para que la tarjeta no esté pegada a los bordes
        elevation = CardDefaults.cardElevation(4.dp) // Añadir una pequeña sombra
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Padding interno de la tarjeta
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto para "Partida personalizada"
            Text(text = "+ Partida personalizada")
            
            // Botón para "Iniciar partida"
            Button(onClick = { /* Acción para crear partida personalizada */ }) {
                Text(text = "Iniciar Partida")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}