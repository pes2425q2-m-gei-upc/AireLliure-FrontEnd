// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.CurrentUser
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString


@Composable
fun ChatCreateScreen(
    onChatCreated: (chatId: Int, userName: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChatCreateViewModel = viewModel()
) {
    val amistats = viewModel.amistats
    val error = viewModel.errorMessage

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarAmistats()
        viewModel.carregarXats()
        viewModel.iniciarWebSocket()
    }

    LaunchedEffect(amistats) {
        viewModel.carregarAmistats()
        viewModel.carregarXats()
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {

            Text(text = (getString(context, R.string.creaconv, selectedLanguage)), style = MaterialTheme.typography.headlineSmall)
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
                Text(text = (getString(context, R.string.volver, selectedLanguage)))
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
