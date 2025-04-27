package com.front_pes.features.screens.map

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R

const val RutasDetailScreenDestination = "RutasDetail"

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ComentariUsuari(nom: String, rating: Int, comentari: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = "User")
            Spacer(modifier = Modifier.width(8.dp))
            Text(nom, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            repeat(rating) {
                Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
            }
        }
        Text(text = comentari)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RutaDetailScreen(onBack: () -> Unit,
                     ) {
    val viewModel: MapViewModel = viewModel()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    val valoracions = remember { mutableStateListOf<Pair<String, Int>>() }
    val comentaris = remember { mutableStateListOf<String>() }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Afegeix la teva valoració") },
            text = {
                Column {
                    Text("Selecciona una puntuació:")
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { selectedRating = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella $i",
                                    tint = if (i <= selectedRating) Color.Yellow else Color.Gray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentari") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Aquí podrías guardar la valoración si tienes lógica
                    valoracions.add("Tu" to selectedRating)
                    comentaris.add(comentario)
                    showRatingDialog = false
                    selectedRating = 0
                    comentario = ""
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRatingDialog = false
                    selectedRating = 0
                    comentario = ""
                }) {
                    Text("Cancel·lar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                modifier = Modifier.height(75.dp), // 🔽 TopBar más estrecha
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.placacatalunya),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = "Ruta Plaça Catalunya",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Distància: ")
                        }
                        append("10 km")
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Punt Inicial: ")
                        }
                        append("4.100")
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Descripció: ")
                        }
                        append("Ruta molt completa amb diferents llocs emblemàtics de Barcelona a visitar.")
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07F285))
                ) {
                    Text("Iniciar Ruta", color = Color.Black)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07F285))
                ) {
                    Text("Finalitzar Ruta", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Valoraciones
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "Valoracions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("4.0")
                    Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
                    Text("(25)")
                }

                // Lista de comentarios (ejemplo)
                valoracions.forEachIndexed { index, (nom, rating) ->
                    ComentariUsuari(nom, rating, comentaris[index])
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showRatingDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07F285))
            ) {
                Text("Afegeix una valoració!", color = Color.Black)
            }
        }


    }
}

