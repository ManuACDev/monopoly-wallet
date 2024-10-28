package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.PickledBluewood
import com.example.wallet.ui.theme.Vulcan


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedAppBar(title: String, showBackButton: Boolean = false, onBack: (() -> Unit)? = null, showLogoutButton: Boolean = false, onLogout: (() -> Unit)? = null) {
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
        navigationIcon = {
            if (showBackButton) { // Mostrar el botón de retroceso si está habilitado
                run {
                    IconButton(onClick = { onBack?.invoke() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Ícono de retroceso
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        actions = {
            if (showLogoutButton) {
                IconButton(
                    onClick = { onLogout?.invoke() },
                    modifier = Modifier
                        .background(color = PickledBluewood, shape = CircleShape)
                        //.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Nepal
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Vulcan // Color de fondo personalizado
        ),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(56.dp) // Definir la altura de la TopAppBar
    )
}
