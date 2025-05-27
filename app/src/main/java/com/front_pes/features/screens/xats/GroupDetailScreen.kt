// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.ActivitatsEvents.ActivityResponse
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun convertToISO(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }
}

fun formatISOToReadable(dateString: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString ?: "")
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "-"
    }
}

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
    var showCreateDialog by remember { mutableStateOf(false) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
        viewModel.iniciarWebSocket(groupId)
        viewModel.carregar_activitats(groupId)
    }

    LaunchedEffect(viewModel.membres) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
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
            var selectedActivitat by remember { mutableStateOf<ActivityResponse?>(null) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activitats del grup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear activitat")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.activitats) { activitat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedActivitat = activitat },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = activitat.nom,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Del ${formatISOToReadable(activitat.data_inici)} al ${formatISOToReadable(activitat.data_fi)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Límit: ${activitat.limit}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (!activitat.descripcio.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activitat.descripcio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            selectedActivitat?.let { activitat ->
                EventDetailsDialog2(
                    nom = activitat.nom,
                    descripcio = activitat.descripcio,
                    dataInici = activitat.data_inici,
                    dataFi = activitat.data_fi,
                    onDismiss = { selectedActivitat = null }
                )
            }

            if (showCreateDialog) {
                CreatePrivateActivityDialog(
                    groupId = groupId,
                    onDismiss = { showCreateDialog = false },
                    onSubmit = { nom, descripcio, inici, fi ->
                        viewModel.crearActivitatPrivada(nom, descripcio, inici, fi, groupId)
                    }
                )
            }
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
                        Column {
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
}

@Composable
fun EventDetailsDialog2(
    nom: String?,
    descripcio: String?,
    dataInici: String?,
    dataFi: String?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detalls de l'activitat", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nom: ${nom ?: "Desconegut"}")
                Text("Descripció: ${descripcio ?: "-"}")
                Text("Data inici: ${com.front_pes.features.screens.ActivitatsEvents.formatISOToReadable(dataInici) ?: "-"}")
                Text("Data fi: ${com.front_pes.features.screens.ActivitatsEvents.formatISOToReadable(dataFi) ?: "-"}")


                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Tancar")
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePrivateActivityDialog(
    groupId: Int,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var nom by remember { mutableStateOf("") }
    var descripcio by remember { mutableStateOf("") }
    var dataInici by remember { mutableStateOf("") }
    var dataFi by remember { mutableStateOf("") }


    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val showDatePicker = { onDateSelected: (String) -> Unit ->
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nova Activitat", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcio,
                    onValueChange = { descripcio = it },
                    label = { Text("Descripció") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = dataInici,
                        onValueChange = {},
                        label = { Text("Data Inici") },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker { selected -> dataInici = selected } },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = dataFi,
                        onValueChange = {},
                        label = { Text("Data Fi") },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker { selected -> dataFi = selected } },
                        enabled = false
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel·lar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (nom.isBlank() || descripcio.isBlank() || dataInici.isBlank() || dataFi.isBlank()) {
                            Toast.makeText(
                                context,
                                "Tots els camps són obligatoris",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val iniciISO = convertToISO(dataInici)
                            val fiISO = convertToISO(dataFi)
                            onSubmit(nom, descripcio, iniciISO, fiISO)
                            onDismiss()
                        }
                    }) {
                        Text("Crear")
                    }
                }
            }
        }
    }
}
