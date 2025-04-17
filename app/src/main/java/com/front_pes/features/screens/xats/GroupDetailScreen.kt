package com.front_pes.features.screens.xats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser

@Composable
fun GroupDetailScreen(
    groupId: Int,
    onBack: () -> Unit,
    viewModel: GroupDetailViewModel = viewModel()
) {
    val isAdmin = viewModel.creador == CurrentUser.correu
    var showDropdown by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Detalls del grup", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        // NOM
        if (isAdmin) {
            OutlinedTextField(
                value = viewModel.nom,
                onValueChange = { viewModel.nom = it },
                label = { Text("Nom del grup") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Nom del grup", style = MaterialTheme.typography.labelMedium)
            Text(viewModel.nom, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // DESCRIPCIÓ
        if (isAdmin) {
            OutlinedTextField(
                value = viewModel.descripcio,
                onValueChange = { viewModel.descripcio = it },
                label = { Text("Descripció") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Descripció", style = MaterialTheme.typography.labelMedium)
            Text(
                text = viewModel.descripcio.ifEmpty { "Sense descripció" },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TÍTOL + BOTÓ AFEGIR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Membres:", style = MaterialTheme.typography.titleMedium)

            if (isAdmin) {
                Button(onClick = { showDropdown = !showDropdown }) {
                    Text("Afegir membre")
                }
            }
        }

        // LLISTA DE MEMBRES VISUAL (amb info admin / tu / altres)
        val membresPerMostrar = if (isAdmin) {
            viewModel.membres
        } else {
            (viewModel.membres + viewModel.creador).distinct()
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(membresPerMostrar.filter { it.isNotBlank() }) { correu ->
                val isCurrentUser = correu == CurrentUser.correu
                val isCreador = correu == viewModel.creador
                val nomBase = viewModel.amistats.find { it.correu == correu }?.nom ?: correu

                val nomMostrat = when {
                    isCurrentUser && isCreador -> "Tu (admin)"
                    isCurrentUser -> "Tu"
                    isCreador -> "$nomBase (admin)"
                    else -> nomBase
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = nomMostrat,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (isAdmin && correu != CurrentUser.correu) {
                        TextButton(onClick = {
                            viewModel.toggleMembre(correu)
                        }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // DESPLEGABLE D’AMISTATS (per afegir)
        if (isAdmin) {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                val amistatsDisponibles = viewModel.amistats.filterNot { viewModel.membres.contains(it.correu) }

                if (amistatsDisponibles.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Cap amistat disponible") },
                        onClick = { }
                    )
                } else {
                    amistatsDisponibles.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.nom) },
                            onClick = {
                                viewModel.toggleMembre(user.correu)
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Tornar")
            }

            if (isAdmin) {
                Row {
                    Button(onClick = {
                        viewModel.actualitzarGrup(
                            id = groupId,
                            onSuccess = { onBack() },
                            onError = { println("Error actualitzant grup") }
                        )
                    }) {
                        Text("Guardar canvis")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar grup")
                    }
                }
            }
        }

        // 🧨 Diàleg confirmació eliminació
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminació") },
                text = { Text("Estàs segur que vols eliminar aquest grup? Aquesta acció no es pot desfer.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.esborrarGrup(
                            id = groupId,
                            onSuccess = { onBack() },
                            onError = { println("Error eliminant grup") }
                        )
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel·lar")
                    }
                }
            )
        }
    }
}
