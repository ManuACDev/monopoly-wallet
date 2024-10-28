import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.wallet.services.AuthService
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.PickledBluewood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(modifier: Modifier = Modifier, onLoginSuccess: () -> Unit, authService: AuthService) {
    var selectedTab by remember { mutableStateOf(AuthTab.Login) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text("Your Email", color = Nepal)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email, // Icono de email
                    contentDescription = "Email Icon",
                    tint = Nepal // Cambia el color del ícono si es necesario
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                cursorColor = Nepal, // Color del cursor
                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text("Your Password", color = Nepal)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.EnhancedEncryption, // Icono de candado
                    contentDescription = "Encryption Icon",
                    tint = Nepal // Cambia el color del ícono si es necesario
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                cursorColor = Nepal, // Color del cursor
                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
            ),
            visualTransformation = PasswordVisualTransformation()
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

    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {
                Text("Your Name", color = Nepal)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Person, // Icono de persona
                    contentDescription = "Person Icon",
                    tint = Nepal // Cambia el color del icono si es necesario
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                cursorColor = Nepal, // Color del cursor
                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text("Your Email", color = Nepal)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email, // Icono de email
                    contentDescription = "Email Icon",
                    tint = Nepal // Cambia el color del icono si es necesario
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                cursorColor = Nepal, // Color del cursor
                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text("Your Password", color = Nepal)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.EnhancedEncryption, // Icono de candado
                    contentDescription = "Encryption Icon",
                    tint = Nepal // Cambia el color del icono si es necesario
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Nepal, // Color del texto cuando está enfocado
                unfocusedTextColor = Nepal, // Color del texto cuando no está enfocado
                cursorColor = Nepal, // Color del cursor
                focusedContainerColor = PickledBluewood, // Color de fondo cuando está enfocado
                unfocusedContainerColor = PickledBluewood, // Color de fondo cuando no está enfocado
                focusedIndicatorColor = PickledBluewood, // Color del borde cuando está enfocado
                unfocusedIndicatorColor = PickledBluewood // Color del borde cuando no está enfocado
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Intentar registrarse
            CoroutineScope(Dispatchers.IO).launch {
                val result = authService.register(name, email, password)
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
