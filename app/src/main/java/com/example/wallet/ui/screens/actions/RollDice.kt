package com.example.wallet.ui.screens.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.ui.theme.TwilightBlue

@Composable
fun RollDiceScreen(modifier: Modifier = Modifier) {
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
            color = TwilightBlue
        )

        // Aquí puedes agregar lógica para lanzar el dado
        Button(onClick = { /* Lógica para lanzar el dado */ }) {
            Text(text = "Roll Dice")
        }
    }
}