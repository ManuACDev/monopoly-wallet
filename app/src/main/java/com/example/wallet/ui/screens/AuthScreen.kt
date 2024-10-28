import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.wallet.services.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit, authService: AuthService) {
    var selectedTab by remember { mutableStateOf(AuthTab.Login) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tabs para Login y Register
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            Tab(
                text = { Text("Login") },
                selected = selectedTab == AuthTab.Login,
                onClick = { selectedTab = AuthTab.Login }
            )
            Tab(
                text = { Text("Register") },
                selected = selectedTab == AuthTab.Register,
                onClick = { selectedTab = AuthTab.Register }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar la tab seleccionada
        when (selectedTab) {
            AuthTab.Login -> LoginTab(onLoginSuccess = onLoginSuccess, authService = authService)
            AuthTab.Register -> RegisterTab(onRegisterSuccess = onLoginSuccess, authService = authService)
        }
    }
}

@Composable
fun LoginTab(onLoginSuccess: () -> Unit, authService: AuthService) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Login", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Intentar iniciar sesión
            CoroutineScope(Dispatchers.IO).launch {
                val result = authService.login(email, password)
                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        onLoginSuccess()
                    }.onFailure {
                        errorMessage = it.message
                    }
                }
            }
        }) {
            Text("Login")
        }

        errorMessage?.let { Text(it, color = Color.Red) }
    }
}

@Composable
fun RegisterTab(onRegisterSuccess: () -> Unit, authService: AuthService) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Register", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Intentar registrarse
            CoroutineScope(Dispatchers.IO).launch {
                val result = authService.register(email, password)
                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        onRegisterSuccess()
                    }.onFailure {
                        errorMessage = it.message
                    }
                }
            }
        }) {
            Text("Register")
        }

        errorMessage?.let { Text(it, color = Color.Red) }
    }
}

// Enum for switching between tabs
enum class AuthTab { Login, Register }
