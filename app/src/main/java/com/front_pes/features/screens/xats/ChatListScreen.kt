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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.front_pes.R


const val ChatListScreen = "ChatListScreen"
const val ChatListScreenDestination = "chats"

@Composable
fun FotoUsuari(url: String?) {
    AsyncImage(
        model = url ?: "", // por si es null
        contentDescription = "user picture",
        modifier = Modifier
            .size(40.dp)
            .padding(end = 16.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // imagen por defecto mientras carga
        error = painterResource(R.drawable.ic_user)         // imagen por defecto si falla
    )
}
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

    Column(modifier = Modifier.fillMaxSize().padding(top = 80.dp, start = 10.dp, end = 10.dp)) {


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
                Text(
                    "Crear Conversació",
                    color = MaterialTheme.colorScheme.onSurface
                    )
            }

            Button(
                onClick = onCrearGrupClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Crear Grup",
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                .padding(bottom = 120.dp) // Espacio final por si hay botones
        ) {
            items(chatList) { chat ->
                if(chat.imatge != null) {
                    ChatListItemindiv(name = chat.nom, imatgeUrl = chat.imatge) {
                        onChatClick(chat.id, chat.nom)
                    }
                } else {
                    ChatListItem(name = chat.nom) {
                        onChatClick(chat.id, chat.nom)
                    }
                }
            }
        }

    }
}

@Composable
fun ChatListItem(name: String,  onClick: () -> Unit) {
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
@Composable
fun ChatListItemindiv(name: String, imatgeUrl: String?,  onClick: () -> Unit) {
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

            if(imatgeUrl != null)FotoUsuari(url = imatgeUrl)

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
