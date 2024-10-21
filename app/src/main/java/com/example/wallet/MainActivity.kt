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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallet.ui.screens.CenterAlignedAppBar
import com.example.wallet.ui.screens.GameOptions
import com.example.wallet.ui.screens.GameScreen
import com.example.wallet.ui.screens.HomeScreen
import com.example.wallet.ui.theme.MonopolyWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            /*MonopolyWalletTheme {
                // Usamos Scaffold para gestionar la estructura de la pantalla
                Scaffold(
                    topBar = { CenterAlignedAppBar(title = "Monopoly Wallet") }, // Título toolbar
                    content = { innerPadding ->
                        HomeScreen(modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize())
                    }
                )
            }*/
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MonopolyWalletTheme {
        val navController = rememberNavController()

        // Scaffold es la estructura general de la app, si tienes una barra superior, etc.
        Scaffold(
            topBar = { CenterAlignedAppBar(title = "Monopoly Wallet") },
            content = { innerPadding ->
                // Sistema de navegación
                NavHost(
                    navController = navController,
                    startDestination = "home" // Pantalla inicial
                ) {
                    composable("home") {
                        HomeScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            navController = navController // Pasar el controlador de navegación
                        )
                    }

                    composable("game_options") {
                        GameOptions(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            navController = navController
                        )
                    }

                    composable(
                        "gameScreen/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        if (gameId != null) {
                            GameScreen(gameId = gameId)
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MonopolyWalletTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}