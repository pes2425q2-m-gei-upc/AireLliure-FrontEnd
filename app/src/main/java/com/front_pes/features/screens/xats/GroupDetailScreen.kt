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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString

@Composable
fun GroupDetailScreen(
    groupId: Int,
    onBack: () -> Unit,
    viewModel: GroupDetailViewModel = viewModel()
) {
    val isAdmin = viewModel.creador == CurrentUser.correu
    var showDropdown by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
        viewModel.iniciarWebSocket(groupId)
    }

    LaunchedEffect(viewModel.membres) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(text = (getString(context, R.string.detgrup, selectedLanguage)), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        // NOM
        if (isAdmin) {
            OutlinedTextField(
                value = viewModel.nom,
                onValueChange = { viewModel.nom = it },
                label = { Text(text = (getString(context, R.string.nomgrup, selectedLanguage))) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(text = (getString(context, R.string.nomgrup, selectedLanguage)), style = MaterialTheme.typography.labelMedium)
            Text(viewModel.nom, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // DESCRIPCIÓ
        if (isAdmin) {
            OutlinedTextField(
                value = viewModel.descripcio,
                onValueChange = { viewModel.descripcio = it },
                label = { Text(text = (getString(context, R.string.desc, selectedLanguage))) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(text = (getString(context, R.string.desc, selectedLanguage)), style = MaterialTheme.typography.labelMedium)
            Text(
                text = viewModel.descripcio.ifEmpty { (getString(context, R.string.sindesc, selectedLanguage)) },
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
            Text(text = (getString(context, R.string.mem, selectedLanguage)), style = MaterialTheme.typography.titleMedium)

            if (isAdmin) {
                Button(onClick = { showDropdown = !showDropdown }) {
                    Text(text = (getString(context, R.string.addmem, selectedLanguage)))
                }
            }
        }

        // LLISTA DE MEMBRES
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
                            Text(text = (getString(context, R.string.elim, selectedLanguage)), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // DESPLEGABLE AFEGIR MEMBRE
        if (isAdmin) {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                val amistatsDisponibles = viewModel.amistats.filterNot { viewModel.membres.contains(it.correu) }

                if (amistatsDisponibles.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(text = (getString(context, R.string.noamic, selectedLanguage))) },
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

        // BOTONS BOTTOM
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text(text = (getString(context, R.string.volver, selectedLanguage)))
            }

            when {
                isAdmin -> {
                    Row {
                        Button(onClick = {
                            viewModel.actualitzarGrup(
                                id = groupId,
                                onSuccess = { onBack() },
                                onError = { println("Error actualitzant grup") }
                            )
                        }) {
                            Text(text = (getString(context, R.string.guardcamb, selectedLanguage)))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = (getString(context, R.string.elimgrup, selectedLanguage)))
                        }
                    }
                }

                else -> {
                    OutlinedButton(
                        onClick = { showLeaveDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = (getString(context, R.string.abgrup, selectedLanguage)))
                    }
                }
            }
        }

        // Diàleg confirmació eliminar grup (ADMIN)
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = (getString(context, R.string.confelim, selectedLanguage))) },
                text = { Text(text = (getString(context, R.string.segelim, selectedLanguage))) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.esborrarGrup(
                            id = groupId,
                            onSuccess = { onBack() },
                            onError = { println("Error eliminant grup") }
                        )
                    }) {
                        Text(text = (getString(context, R.string.elim, selectedLanguage)), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(text = (getString(context, R.string.cancel, selectedLanguage)))
                    }
                }
            )
        }

        // Diàleg confirmació abandonar grup (NO ADMIN)
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                title = { Text(text = (getString(context, R.string.confsort, selectedLanguage))) },
                text = { Text(text = (getString(context, R.string.segsort, selectedLanguage))) },
                confirmButton = {
                    TextButton(onClick = {
                        showLeaveDialog = false
                        viewModel.abandonarGrup(
                            id = groupId,
                            onSuccess = { onBack() },
                            onError = { println("Error abandonant grup") }
                        )
                    }) {
                        Text(text = (getString(context, R.string.sort, selectedLanguage)), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveDialog = false }) {
                        Text(text = (getString(context, R.string.cancel, selectedLanguage)))
                    }
                }
            )
        }
    }
}
