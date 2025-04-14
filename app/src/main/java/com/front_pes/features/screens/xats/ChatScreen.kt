package com.front_pes.features.screens.xats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import java.text.SimpleDateFormat
import java.util.*

const val ChatScreenDestination = "chat"

@Composable
fun ChatScreen(chatId: Int, viewModel: ChatDetailViewModel = viewModel()) {
    val missatges = viewModel.missatges
    val error = viewModel.errorMessage
    var newMessage by remember { mutableStateOf("") }
    val autor = CurrentUser.correu
    val listState = rememberLazyListState()

    var showEditDialog by remember { mutableStateOf(false) }
    var mensajeSeleccionado by remember { mutableStateOf<ChatDetailViewModel.Missatge?>(null) }
    var nuevoTexto by remember { mutableStateOf("") }

    var showMenuForMessageId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(chatId) {
        viewModel.carregarMissatges(chatId)
    }

    LaunchedEffect(missatges.size) {
        listState.animateScrollToItem(missatges.size)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Xat ID: $chatId",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        if (error != null) {
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(missatges.sortedBy { it.data }) { msg ->
                val esMeu = msg.autor == autor

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = msg.autor ?: "Anònim",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (esMeu) {
                                Box {
                                    IconButton(onClick = {
                                        showMenuForMessageId = msg.id
                                        mensajeSeleccionado = msg
                                        nuevoTexto = msg.text
                                    }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Opcions")
                                    }

                                    DropdownMenu(
                                        expanded = showMenuForMessageId == msg.id,
                                        onDismissRequest = { showMenuForMessageId = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Editar") },
                                            onClick = {
                                                showEditDialog = true
                                                showMenuForMessageId = null
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Eliminar") },
                                            onClick = {
                                                mensajeSeleccionado?.let { msg ->
                                                    viewModel.esborrarMissatge(
                                                        missatgeId = msg.id,
                                                        onSuccess = {
                                                            viewModel.carregarMissatges(chatId)
                                                            showMenuForMessageId = null
                                                        },
                                                        onError = {
                                                            println("Error eliminant: $it")
                                                            showMenuForMessageId = null
                                                        }
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = msg.data,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        if (showEditDialog && mensajeSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar missatge") },
                text = {
                    TextField(
                        value = nuevoTexto,
                        onValueChange = { nuevoTexto = it },
                        label = { Text("Missatge") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.editarMissatge(
                            missatgeOriginal = mensajeSeleccionado!!,
                            textNou = nuevoTexto,
                            onSuccess = {
                                showEditDialog = false
                                viewModel.carregarMissatges(chatId)
                            },
                            onError = {
                                showEditDialog = false
                                println("Error editant: $it")
                            }
                        )
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escriu un missatge") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (newMessage.isNotBlank()) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    formatter.timeZone = TimeZone.getTimeZone("Europe/Madrid")
                    val dataActual = formatter.format(Date())

                    viewModel.enviarMissatge(
                        text = newMessage,
                        xat = chatId,
                        autor = autor,
                        onSuccess = {
                            newMessage = ""
                            viewModel.carregarMissatges(chatId)
                        },
                        onError = {
                            println("Error enviant: $it")
                        }
                    )
                }
            }) {
                Text("Enviar")
            }
        }
    }
}
