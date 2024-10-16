package com.example.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wallet.ui.screens.CenterAlignedAppBar
import com.example.wallet.ui.screens.HomeScreen
import com.example.wallet.ui.theme.MonopolyWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonopolyWalletTheme {
                // Usamos Scaffold para gestionar la estructura de la pantalla
                Scaffold(
                    topBar = { CenterAlignedAppBar(title = "Monopoly Wallet") }, // Título toolbar
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Aquí cambiamos el contenido de Greeting a HomeScreen
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MonopolyWalletTheme {
        HomeScreen()
    }
}