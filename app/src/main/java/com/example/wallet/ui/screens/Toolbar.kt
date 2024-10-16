package com.example.wallet.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedAppBar(title: String) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Centrar el texto vertical y horizontalmente
            ) {
                Text(
                    text = title,
                    color = Color.White, // Texto en color blanco
                    textAlign = TextAlign.Center, // Centrar el texto horizontalmente
                    maxLines = 1
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Black // Color de fondo negro
        ),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .height(56.dp) // Definir la altura de la TopAppBar
    )
}
