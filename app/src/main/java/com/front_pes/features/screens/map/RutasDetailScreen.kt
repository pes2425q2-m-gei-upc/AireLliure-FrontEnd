package com.front_pes.features.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

const val RutasDetailScreenDestination = "RutasDetail"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailScreen(ruta: String,
                     onBack: () -> Unit,
                     RutaViewModel: RutaViewModel) {
    val viewModel: MapViewModel = viewModel()
    val ruta by RutaViewModel.selectedRuta.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ruta?.nom ?: "Ruta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    )
    { innerPadding ->
        when {
            errorMessage != null -> {
                Text("Error: $errorMessage")
            }

            ruta == null -> {
                CircularProgressIndicator()
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        "Descripción: ${ruta?.descripcio ?: "Desconocida"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Distancia: ${ruta?.dist_km ?: "?"} km",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "ID del Punto de Inicio: ${ruta?.punt_inici ?: "?"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}