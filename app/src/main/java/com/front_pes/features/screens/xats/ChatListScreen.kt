package com.front_pes.features.screens.xats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

const val ChatListScreen = "ChatListScreen"
const val ChatListScreenDestination = "chats"

@Composable
fun ChatListScreen(
    onChatClick: (chatId: Int, userName: String) -> Unit,
    onNovaConversacioClick: () -> Unit,
    onCrearGrupClick: () -> Unit,
    viewModel: XatViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.carregarXats()
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.carregarXats()
        }
    }
    val chatList = viewModel.xats

    Column(modifier = Modifier.fillMaxSize().padding(top = 80.dp, start = 10.dp, end = 24.dp)) {
        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNovaConversacioClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Crear Conversació")
            }

            Button(
                onClick = onCrearGrupClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Crear Grup")
            }
        }

        Text(
            text = "Xats",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 80.dp) // Espacio final por si hay botones
        ) {
            items(chatList) { chat ->
                ChatListItem(name = chat.nom) {
                    onChatClick(chat.id, chat.nom)
                }
            }
        }
    }
}

@Composable
fun ChatListItem(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
