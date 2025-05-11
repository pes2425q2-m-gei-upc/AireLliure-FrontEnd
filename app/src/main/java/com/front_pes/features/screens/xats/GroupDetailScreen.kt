package com.front_pes.features.screens.xats

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.features.screens.ActivitatsEvents.ActivityResponse
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


    LaunchedEffect(Unit) {
        viewModel.carregarGrup(groupId)
        viewModel.carregarAmistats()
        viewModel.carregar_activitats(groupId)
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
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
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
                onDismiss = { showCreateDialog = false },
                onSubmit = {nom, descripcio, inici, fi ->
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
                Text("Tornar")
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

                else -> {
                    OutlinedButton(
                        onClick = { showLeaveDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Abandonar grup")
                    }
                }
            }
        }

        // Diàleg confirmació eliminar grup (ADMIN)
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

        // Diàleg confirmació abandonar grup (NO ADMIN)
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                title = { Text("Confirmar sortida") },
                text = { Text("Estàs segur que vols abandonar aquest grup?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLeaveDialog = false
                        viewModel.abandonarGrup(
                            id = groupId,
                            onSuccess = { onBack() },
                            onError = { println("Error abandonant grup") }
                        )
                    }) {
                        Text("Sortir", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveDialog = false }) {
                        Text("Cancel·lar")
                    }
                }
            )
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
                            Toast.makeText(context, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show()
                        } else {
                            onSubmit(nom, descripcio, dataInici, dataFi)
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
