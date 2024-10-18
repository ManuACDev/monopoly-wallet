package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    // Crear la pantalla con un layout vertical
    Column(modifier = modifier
        .fillMaxSize() // La pantalla ocupa todo el tamaño disponible
        .padding(16.dp), // Padding a toda la pantalla
        verticalArrangement = Arrangement.Top, // Coloca los elementos en la parte superior
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
    ) {
        CustomCard(onCardClick = { navController.navigate("game_options") })
    }
}

@Composable
fun CustomCard(onCardClick: () -> Unit) {

    // Crear la tarjeta
    Card(modifier = Modifier
        .fillMaxWidth() // La tarjeta ocupará todo el ancho
        .height(125.dp) // Define un alto específico para la tarjeta
        .padding(8.dp) // Padding para que la tarjeta no esté pegada a los bordes
        .clickable { onCardClick() }, // Navegar a la pantalla de opciones de juego
        elevation = CardDefaults.cardElevation(4.dp), // Añadir una pequeña sombra
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f)), // Fondo gris claro con transparencia
        shape = RoundedCornerShape(8.dp) // Esquinas redondeadas
    ) {
        // Crear una fila (Row) para colocar el "+" y el texto en la misma línea
        Row(modifier = Modifier
            .fillMaxSize() // Llenar el tamaño de la tarjeta
            .padding(16.dp), // Padding interno de la tarjeta
            verticalAlignment = Alignment.CenterVertically, // Alinear verticalmente el contenido
            horizontalArrangement = Arrangement.Center // Centrar horizontalmente
        ) {
            // Crear el símbolo "+" con fondo redondo y gris oscuro
            Box(modifier = Modifier
                .size(50.dp) // Tamaño del círculo
                .background(color = Color(0xFFD0D0D0), shape = CircleShape), // Fondo gris claro y redondo
                contentAlignment = Alignment.Center // Centrar el contenido dentro del círculo
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp, // Tamaño del texto más grande
                    color = Color.White // Color del texto blanco
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Separar el "+" del texto

            // Texto para "Partida personalizada" con estilo más grande y atractivo
            Text(
                text = "PARTIDA PERSONALIZADA",
                fontSize = 18.sp, // Tamaño de la fuente más grande
                fontWeight = FontWeight.Bold, // Texto en negrita
                color = Color.Black // Color del texto negro
            )
        }
    }

}

/*@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameOptionsDialog(onDismiss: () -> Unit) {
    var numPlayers by remember { mutableStateOf(2) }
    var initialMoney by remember { mutableStateOf("300000") }
    var passGoMoney by remember { mutableStateOf("40000") }
    var isBankAutomatic by remember { mutableStateOf(false) }
    var selectedBanker by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        // Contenido del diálogo
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    // Título del diálogo
                    Text(
                        text = "Opciones de Partida",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                item {
                    // Número de jugadores
                    Text(text = "Número de Jugadores")

                    // FlowRow para organizar los botones
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center, // Centramos los botones horizontalmente
                        verticalArrangement = Arrangement.SpaceBetween, // Espacio vertical entre filas
                        maxItemsInEachRow = 3 // Mostrar 3 botones por fila
                    ) {
                        for (i in 2..6) {
                            Button(
                                modifier = Modifier.padding(5.dp),
                                onClick = { numPlayers = i },
                                colors = if (numPlayers == i) ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                ) else ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "$i")
                            }
                        }
                    }
                }

                item {
                    // Dinero inicial
                    Text(text = "Dinero Inicial")
                    OutlinedTextField(
                        value = initialMoney,
                        onValueChange = { initialMoney = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(0.75f) // Ocupa el 75% del ancho disponible
                    )
                }

                item {
                    // Dinero por pasar la salida
                    Text(text = "Dinero por Pasar la Salida")
                    OutlinedTextField(
                        value = passGoMoney,
                        onValueChange = { passGoMoney = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(0.75f) // Ocupa el 75% del ancho disponible
                    )
                }

                item {
                    // Banca automática
                    Text(text = "Banca Automática")
                    Switch(
                        checked = isBankAutomatic,
                        onCheckedChange = { isBankAutomatic = it }
                    )

                    // Si la banca no es automática, elegir un jugador como banquero
                    if (!isBankAutomatic) {
                        Text(text = "Seleccionar Banquero")

                        // Estado para el menú desplegable
                        var expanded by remember { mutableStateOf(false) }

                        // Crear el menú desplegable
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = "Jugador $selectedBanker", // Muestra el jugador seleccionado
                                onValueChange = { /* No es necesario */ },
                                readOnly = true, // No se puede editar el texto
                                label = { Text("Seleccionar Banquero") },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .menuAnchor() // Asegura que el menú se alinee correctamente
                            )

                            // Elemento del menú desplegable
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                for (i in 1..numPlayers) {
                                    DropdownMenuItem(onClick = {
                                        selectedBanker = i // Selecciona el jugador como banquero
                                        expanded = false // Cierra el menú después de seleccionar
                                    }, text = { Text(text = "Jugador $i") }) // Usando text como un parámetro
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón para crear la partida
                    Button(
                        onClick = {
                            //onCreateGame(numPlayers, initialMoney, passGoMoney, selectedBanker)
                            println("Iniciando partida con $numPlayers jugadores")
                            onDismiss() // Cierra el diálogo después de crear la partida
                        },
                        // modifier = Modifier.align(Alignment.CenterHorizontally) // Centrar el botón
                    ) {
                        Text("Crear Partida")
                    }

                    // Botón para cerrar el diálogo
                    Button(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
            } // Cierra Column
        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}