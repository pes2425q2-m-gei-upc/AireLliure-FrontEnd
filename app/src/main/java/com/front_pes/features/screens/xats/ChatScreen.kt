package com.front_pes.features.screens.xats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import java.text.SimpleDateFormat
import java.util.*

const val ChatScreenDestination = "chat"

@Composable
fun ChatScreen(chatId: Int, userName: String, viewModel: ChatDetailViewModel = viewModel(), onNavigateToGroupDetail: (Int) -> Unit = {}) {
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
        viewModel.detectarSiEsGrup(chatId)

    }

    LaunchedEffect(missatges.size) {
        listState.animateScrollToItem(missatges.size)
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName,
                    modifier = Modifier
                        .padding(16.dp)
                        .then(
                            if (viewModel.isGroup) Modifier.clickable { onNavigateToGroupDetail(chatId) }
                            else Modifier
                        ),
                    color = if (viewModel.isGroup) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )

            }
        }
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
                .padding(horizontal = 8.dp)
        ) {
            items(missatges.sortedBy { it.data }) { msg ->
                val esMeu = msg.autor == autor
                val alignment = if (esMeu) Alignment.End else Alignment.Start
                val bubbleColor = if (esMeu) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color(0xFFEDEDED)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (esMeu) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(bubbleColor, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (msg.autor == CurrentUser.correu) CurrentUser.nom else userName ?: "Anònim",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg.data,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.End),
                            color = Color.Gray
                        )
                    }

                    if (esMeu) {
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
                                    mensajeSeleccionado?.let { m ->
                                        viewModel.esborrarMissatge(
                                            missatgeId = m.id,
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
