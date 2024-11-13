package com.boardgame.polybank

import AuthScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.boardgame.polybank.services.AuthService
import com.boardgame.polybank.services.FirestoreService
import com.boardgame.polybank.ui.screens.CenterAlignedAppBar
import com.boardgame.polybank.ui.screens.GameOptions
import com.boardgame.polybank.ui.screens.GameScreen
import com.boardgame.polybank.ui.screens.HomeScreen
import com.boardgame.polybank.ui.screens.actions.AccessBankScreen
import com.boardgame.polybank.ui.screens.actions.LiveChatScreen
import com.boardgame.polybank.ui.screens.actions.RollDiceScreen
import com.boardgame.polybank.ui.screens.actions.SendMoneyScreen
import com.boardgame.polybank.ui.theme.MonopolyWalletTheme
import com.boardgame.polybank.ui.theme.Vulcan
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authService = AuthService()
    private val firestoreService = FirestoreService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar el SDK de Google Mobile Ads
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {
                // Callback opcional: puedes agregar logs o acciones adicionales aquí si es necesario.
                println("SDK de Google Mobile Ads inicializado correctamente")
            }
        }

        setContent {
            MyApp(authService = authService, firestoreService = firestoreService)
        }
    }
}

@Composable
fun MyApp(authService: AuthService, firestoreService: FirestoreService) {
    MonopolyWalletTheme {
        val navController = rememberNavController()
        // Obtenemos el estado de autenticación
        val isAuthenticated = remember { mutableStateOf(authService.isUserAuthenticated()) }

        LaunchedEffect(isAuthenticated.value) {
            if (isAuthenticated.value) {
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true } // Limpia la pila de navegación
                }
            } else {
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        // Scaffold es la estructura general de la app, si tienes una barra superior, etc.
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Vulcan,
            topBar = {
                val currentBackStackEntry = navController.currentBackStackEntryAsState().value
                val route = currentBackStackEntry?.destination?.route
                val showBackButton = route == "game_options" || route == "roll_dice/{gameId}/{playerName}"
                        || route == "live_chat/{gameId}" || route == "send_money/{gameId}" || route == "access_bank/{gameId}"
                val showLogoutButton = route == "home"

                CenterAlignedAppBar(
                    title = when (route) {
                        "game_options" -> "Game Options"
                        "roll_dice/{gameId}/{playerName}" -> "Roll Dice"
                        "live_chat/{gameId}" -> "Live Chat"
                        "send_money/{gameId}" -> "Send Money"
                        "access_bank/{gameId}" -> "Access Bank"
                        else -> "PolyBank"
                    },
                    showBackButton = showBackButton,
                    onBack = {
                        navController.popBackStack() // Acción para regresar a la pantalla anterior
                    },
                    showLogoutButton = showLogoutButton,
                    onLogout = {
                        authService.logout()
                        isAuthenticated.value = false
                    }
                )
            },
            content = { innerPadding ->
                // Sistema de navegación
                NavHost(
                    navController = navController,
                    startDestination = if (isAuthenticated.value) "home" else "auth" // Pantalla inicial
                ) {
                    composable("auth") {
                        AuthScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            onLoginSuccess = {
                                isAuthenticated.value = true
                            },
                            authService = authService // Pasamos AuthService a AuthScreen
                        )
                    }

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
                            GameScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            gameId = gameId,
                            navController = navController
                            )
                        }
                    }

                    composable(
                        "roll_dice/{gameId}/{playerName}",
                        arguments = listOf(
                            navArgument("gameId") { type = NavType.StringType },
                            navArgument("playerName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        val playerName = backStackEntry.arguments?.getString("playerName")
                        if (gameId != null && playerName != null) {
                            RollDiceScreen(
                                gameId = gameId,
                                playerName = playerName,
                                firestoreService = firestoreService
                            )
                        }
                    }

                    composable(
                        "live_chat/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        if (gameId != null) {
                            LiveChatScreen(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                gameId = gameId
                            )
                        }
                    }

                    composable(
                        "send_money/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                    ) {backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        if (gameId != null) {
                            SendMoneyScreen(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                gameId = gameId
                            )
                        }
                    }

                    composable(
                        "access_bank/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                    ) {backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId")
                        if (gameId != null) {
                            AccessBankScreen(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                gameId = gameId
                            )
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