import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.wallet.services.AuthService
import com.example.wallet.services.InteractionService
import com.example.wallet.ui.theme.Nepal
import com.example.wallet.ui.theme.PickledBluewood
import com.example.wallet.ui.theme.RoyalBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(modifier: Modifier = Modifier, onLoginSuccess: () -> Unit, authService: AuthService) {
    var selectedTab by remember { mutableStateOf(AuthTab.Login) }
    val interactionService = InteractionService(LocalContext.current)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterVertically), // Espaciado automático entre elementos
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tabs para Login y Register
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = PickledBluewood, // Color de fondo de las pestañas
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = Nepal // Color de la barra subrayada
                )
            },
            divider = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Transparent) // Elimina la línea divisoria inferior
                )
            }
        ) {
            AuthTab.entries.forEach { tab ->
                Tab(
                    text = { Text(tab.name, color = if (selectedTab == tab) Nepal else Nepal) },
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    selectedContentColor = Nepal, // Color del texto cuando está seleccionada
                    unselectedContentColor = PickledBluewood // Color del texto cuando no está seleccionada
                )
            }
        }

        // Mostrar la tab seleccionada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp)
        ) {
            when (selectedTab) {
                AuthTab.Login -> LoginTab(onLoginSuccess = onLoginSuccess, authService = authService, interactionService = interactionService)
                AuthTab.Register -> RegisterTab(onRegisterSuccess = onLoginSuccess, authService = authService)
            }
        }
    }
}

@Composable
fun LoginTab(onLoginSuccess: () -> Unit, authService: AuthService, interactionService: InteractionService) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    interactionService.showToast("Logging in...", Toast.LENGTH_SHORT)
                    isButtonEnabled = false

                    Handler(Looper.getMainLooper()).postDelayed({
                        // Intentar iniciar sesión
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val result = authService.login(email, password)
                                withContext(Dispatchers.Main) {
                                    result.onSuccess {
                                        interactionService.showToast("Login successful!", Toast.LENGTH_SHORT)
                                        onLoginSuccess()
                                    }.onFailure {
                                        interactionService.showToast(it.message ?: "Login failed.", Toast.LENGTH_SHORT)
                                        isButtonEnabled = true
                                    }
                                }
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                                interactionService.showToast(e.message ?: "Error during login.", Toast.LENGTH_SHORT)
                                isButtonEnabled = true
                            }
                        }
                    }, 1000L)
                } else {
                    println("Error: Todos los campos son obligatorios.")
                    interactionService.showToast("Please enter email and password.", Toast.LENGTH_SHORT)
                }
            },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoyalBlue,
                disabledContainerColor = PickledBluewood
            )
        ) {
            Text(
                text = "Login",
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                color = Color.White
            )
        }
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
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
        ) {
            Text(
                text = "Register",
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )
        }

        errorMessage?.let { Text(it, color = Color.Red) }
    }
}

// Enum for switching between tabs
enum class AuthTab { Login, Register }
