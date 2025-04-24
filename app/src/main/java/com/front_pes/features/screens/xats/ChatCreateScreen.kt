package com.front_pes.features.screens.xats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatCreateScreen(
    onChatCreated: (chatId: Int, userName: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChatCreateViewModel = viewModel()
) {
    val amistats = viewModel.amistats
    val error = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.carregarAmistats()
        viewModel.carregarXats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crear Conversació", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        val amistatsDisponibles = amistats.filter { amistat ->
            viewModel.xatsExistents.none { xat -> xat.nom == amistat.nom }
        }

        LazyColumn {
            items(amistatsDisponibles) { item ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        viewModel.crearXatIndividual(
                            nom = item.nom,
                            usuari2 = item.correu,
                            onSuccess = { chatId ->
                                onChatCreated(chatId, item.nom)
                            },
                            onError = { println("Error creant xat: $it") }
                        )
                    }
                ) {
                    Text(item.nom)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
            Text("Tornar")
        }
    }
}
