package com.front_pes.features.screens.ActivitatsEvents

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

const val eventScreen = "eventScreen"

enum class Selector {
    ALL,
    ONE
}

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
fun EventScreen(viewModel: eventViewModel = viewModel()) {

    var currentMode by remember { mutableStateOf(Selector.ALL) }
    val scrollState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<ActivityResponse?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.get_all_publiques()
        viewModel.get_participacions()
    }
    val eventList = viewModel.events
    val participacions = viewModel.participacions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp, start = 10.dp, end = 24.dp)
    ) {
        val selectorScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(selectorScroll),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVITATS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.ALL) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { currentMode = Selector.ALL }
                    .padding(10.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Afegir activitat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Cerca activitat pública...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            if (currentMode == Selector.ALL) {
                items(eventList.filter { it.nom.contains(searchText, ignoreCase = true) }) { item ->
                    EventListItem(name = item.nom, onDelete = { viewModel.eliminar_event(item.id) }, onClick = { selectedEvent = item })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showDialog) {
            AddEventDialog(
                onDismiss = { showDialog = false },
                onSubmit = { nom, desc, dataInici, dataFi, limit ->
                    val isoDataInici = convertToISO(dataInici)
                    val isoDataFi = convertToISO(dataFi)
                    viewModel.crear_activitat_event_public(nom,desc,isoDataInici,isoDataFi, limit.toInt())
                    Toast.makeText(context, "Activitat creada!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        selectedEvent?.let { event ->
            EventDetailsDialog(
                event = event,
                onDismiss = { selectedEvent = null },
                onApuntar = {
                    viewModel.apuntarse_activitat(event.id) {
                        selectedEvent = null
                    }
                },
                onAbandonar = {
                    viewModel.abandonar(event.id) {
                        selectedEvent = null
                    }
                },
                onUpdate = { nom, desc, inici, fi, limit ->
                    viewModel.editar_event(event.id, nom, desc, inici, fi, limit)
                },
                participacions = participacions
            )
        }

    }
}

@Composable
fun EventListItem(name: String, onDelete: () -> Unit, onClick: () -> Unit) {
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
                imageVector = Icons.Default.Info,
                contentDescription = "Event Icon",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar Event",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var nom by remember { mutableStateOf("") }
    var descripcio by remember { mutableStateOf("") }
    var dataInici by remember { mutableStateOf("") }
    var dataFi by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val showDatePicker = { onDateSelected: (String) -> Unit ->
        DatePickerDialog(
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

                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Límit de persones") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel·lar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (nom.isBlank() || descripcio.isBlank() || dataInici.isBlank() || dataFi.isBlank() || limit.isBlank()) {
                            Toast.makeText(context, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show()
                        } else {
                            onSubmit(nom, descripcio, dataInici, dataFi, limit)
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

@Composable
fun EventDetailsDialog(
    event: ActivityResponse,
    participacions: List<ActivityResponse>,
    onDismiss: () -> Unit,
    onApuntar: () -> Unit,
    onAbandonar: () -> Unit,
    onUpdate: (String, String, String, String, Int) -> Unit
) {
    val isAuthor = CurrentUser.correu == event.creador_event
    val isParticipant = participacions.any { it.id == event.id }

    var editMode by remember { mutableStateOf(false) }

    var nom by remember { mutableStateOf(event.nom ?: "") }
    var descripcio by remember { mutableStateOf(event.descripcio ?: "") }
    var dataInici by remember { mutableStateOf(formatISOToReadable(event.data_inici) ?: "") }
    var dataFi by remember { mutableStateOf(formatISOToReadable(event.data_fi) ?: "") }
    var limit by remember { mutableStateOf(event.limit?.toString() ?: "") }



    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detalls de l'activitat", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                if (editMode) {
                    OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") })
                    OutlinedTextField(value = descripcio, onValueChange = { descripcio = it }, label = { Text("Descripció") })
                    OutlinedTextField(value = dataInici, onValueChange = { dataInici = it }, label = { Text("Data inici") })
                    OutlinedTextField(value = dataFi, onValueChange = { dataFi = it }, label = { Text("Data fi") })
                    OutlinedTextField(value = limit, onValueChange = { limit = it }, label = { Text("Límit") })
                } else {
                    Text("Nom: $nom")
                    Text("Descripció: $descripcio")
                    Text("Data inici: $dataInici")
                    Text("Data fi: $dataFi")
                    Text("Límit: $limit")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Tancar") }

                    if (isAuthor && !editMode) {
                        TextButton(onClick = { editMode = true }) { Text("Editar") }
                    }

                    if (editMode) {
                        Button(onClick = {
                            (limit.toIntOrNull() ?: event.limit)?.let {
                                onUpdate(
                                    nom,
                                    descripcio,
                                    convertToISO(dataInici),
                                    convertToISO(dataFi),
                                    it
                                )
                            }
                            editMode = false
                            onDismiss()
                        }) {
                            Text("Actualitzar")
                        }
                    } else {
                        Button(onClick = {
                            if (isParticipant) onAbandonar() else onApuntar()
                        }) {
                            Text(if (isParticipant) "Abandonar" else "Apuntar-se")
                        }
                    }
                }
            }
        }
    }
}