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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    playerMoney: Int,
    onSendMoney: (Int) -> Unit,
    onRollDice: () -> Unit
) {
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
                        value = "",
                        onValueChange = { /* Manejar el valor ingresado */ },
                        label = { Text("Cantidad a enviar") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { onSendMoney(10000) }) { // Cambia el monto según sea necesario
                        Text("Enviar")
                    }
                }
            }

            // Botón para tirar los dados
            Button(
                onClick = onRollDice,
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
        playerMoney = 2000,
        onSendMoney = { amount -> println("Enviando $amount") },
        onRollDice = { println("Tirando dados") }
    )
}