@file:Suppress("detekt")
package com.front_pes.features.screens.map

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import kotlinx.coroutines.launch

const val RutasDetailScreenDestination = "RutasDetail"

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun FotoUsuari(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "user picture",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // opcional
        error = painterResource(R.drawable.ic_user)         // opcional
    )
}

@Composable
fun ComentariUsuari(
    nom: String,
    rating: Int,
    comentari: String,
    esMeu: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    imatge: String?
) {

        var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

        Column(modifier = Modifier
            .padding(16.dp)
        ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            if (imatge != null) {
                com.front_pes.features.screens.xamistat.FotoUsuari(url = imatge)
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 5.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(nom, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            repeat(rating) {
                Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
            }
            if (esMeu) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opcions")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = (getString(context, R.string.edit, selectedLanguage))) },
                        onClick = {
                            expanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = (getString(context, R.string.elim, selectedLanguage))) },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
        Text(text = comentari)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClasificacioDialog(
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit,
    currentDificultat: String,
    currentAccesibilitat: String
) {
    var selectedDificultat by remember { mutableStateOf(currentDificultat) }
    var expandedDificultat by remember { mutableStateOf(false) }

    var selectedAccesibilitat by remember { mutableStateOf(currentAccesibilitat) }
    var expandedAccesibilitat by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = (getString(context, R.string.clasru, selectedLanguage))) },
        text = {
            Column {
                Text(text = (getString(context, R.string.dificultat, selectedLanguage)))
                ExposedDropdownMenuBox(
                    expanded = expandedDificultat,
                    onExpandedChange = { expandedDificultat = !expandedDificultat }
                ) {
                    TextField(
                        value = selectedDificultat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = (getString(context, R.string.seldif, selectedLanguage))) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDificultat) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDificultat,
                        onDismissRequest = { expandedDificultat = false }
                    ) {
                        listOf("Alta", "Media", "Baja").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedDificultat = option
                                    expandedDificultat = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = (getString(context, R.string.accesresp, selectedLanguage)))
                ExposedDropdownMenuBox(
                    expanded = expandedAccesibilitat,
                    onExpandedChange = { expandedAccesibilitat = !expandedAccesibilitat }
                ) {
                    TextField(
                        value = selectedAccesibilitat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = (getString(context, R.string.selac, selectedLanguage))) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAccesibilitat) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAccesibilitat,
                        onDismissRequest = { expandedAccesibilitat = false }
                    ) {
                        listOf("Baja", "Moderada").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedAccesibilitat = option
                                    expandedAccesibilitat = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Aquí puedes guardar los valores seleccionados
                onGuardar(selectedDificultat, selectedAccesibilitat)            }) {
                Text(text = (getString(context, R.string.guard, selectedLanguage)))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = (getString(context, R.string.cancel, selectedLanguage)))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RutasDetailScreen(onBack: () -> Unit, ruta_id: Int) {
    val viewModel: RutaViewModel = viewModel()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    val tota_info = viewModel.all_info_ruta
    val mitjana = viewModel.mitjanaValoracions
    val totalValoracions = viewModel.nombreValoracions
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    BackHandler {
        onBack()
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var valoracioSeleccionada by remember { mutableStateOf<valoracions?>(null) }
    var nuevoComent by remember { mutableStateOf("") }
    var nuevoRating by remember { mutableStateOf(0f) }

    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit){
        viewModel.get_informacio_ruta(ruta_id)
        viewModel.getAssignacionsRuta(ruta_id)
    }


    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text(text = (getString(context, R.string.aval, selectedLanguage))) },
            text = {
                Column {
                    Text(text = (getString(context, R.string.spunt, selectedLanguage)))
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { selectedRating = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella $i",
                                    tint = if (i <= selectedRating) Color.Yellow else Color.Gray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text(text = (getString(context, R.string.coment, selectedLanguage))) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Aquí podrías guardar la valoración si tienes lógica
                    if (selectedRating in 1..5 && comentario.isNotBlank()) {
                        viewModel.afegir_valoracio(
                            user_id = CurrentUser.correu, // Ajusta esto a cómo tengas el ID del usuario
                            ruta_id = ruta_id,
                            puntuacio_ = selectedRating.toFloat(),
                            comentari_ = comentario
                        )
                    }
                    showRatingDialog = false
                    selectedRating = 0
                    comentario = ""
                }) {
                    Text(text = (getString(context, R.string.confirm, selectedLanguage)))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRatingDialog = false
                    selectedRating = 0
                    comentario = ""
                }) {
                    Text(text = (getString(context, R.string.cancel, selectedLanguage)))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                modifier = Modifier.height(75.dp), // 🔽 TopBar más estrecha
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            /*
            Image(
                painter = painterResource(id = R.drawable.placacatalunya),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

             */

            Text(
                "${tota_info?.nom ?: "Desconocida"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append((getString(context, R.string.descruta, selectedLanguage)) + ": ")
                        }
                        append(viewModel.obtenirItinerariAmbDescripcio())
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append((getString(context, R.string.Dist, selectedLanguage)) + ": ")
                        }
                        append(viewModel.extreureDistanciaDescripcio() + " m")
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append((getString(context, R.string.pini, selectedLanguage)) + ": ")
                        }
                        append(tota_info?.punt_inici?.toString() ?: "Desconeguda")
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                    if(viewModel.dificultatRuta != null) {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(
                                    (getString(
                                        context,
                                        R.string.dificultat,
                                        selectedLanguage
                                    )) + ": "
                                )
                            }
                            append(viewModel.dificultatRuta)
                        })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if(viewModel.accesibilitatRuta != null) {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(
                                    (getString(
                                        context,
                                        R.string.accesresp,
                                        selectedLanguage
                                    )) + ": "
                                )
                            }
                            append(viewModel.accesibilitatRuta)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (CurrentUser.administrador) {
                if(viewModel.dificultatRuta == null || viewModel.accesibilitatRuta == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07F285))
                        ) {
                            Text(
                                text = (getString(context, R.string.clasr, selectedLanguage)),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            if (showDialog) {
                ClasificacioDialog(
                    currentDificultat = viewModel.dificultatRuta ?: "",
                    currentAccesibilitat = viewModel.accesibilitatRuta ?: "",
                    onDismiss = { showDialog = false },
                    onGuardar = { dificultat, accesibilitat ->
                        viewModel.guardarClassificacio(dificultat, accesibilitat, ruta_id)
                        viewModel.getAssignacionsRuta(ruta_id)
                        showDialog = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Valoraciones
            Column {
                Text(
                    text = (getString(context, R.string.valo, selectedLanguage)),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(String.format("%.1f", mitjana))
                    Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.Yellow)
                    Text("($totalValoracions)")
                }
                Spacer(modifier = Modifier.height(15.dp))
                // Lista de comentarios (ejemplo)
                Box(
                    modifier = Modifier
                        .height(200.dp) // Puedes ajustar la altura según diseño
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState()) // También podrías usar LazyColumn
                ) {
                    Column {
                        viewModel.valoracions.forEach { valoracio ->
                            ComentariUsuari(
                                nom = valoracio.nom_usuari,
                                rating = valoracio.puntuacio.toInt(),
                                imatge = valoracio.imatge_usuari,
                                comentari = valoracio.comentari,
                                esMeu = valoracio.usuari == CurrentUser.correu,
                                onEdit = {
                                    valoracioSeleccionada = valoracio
                                    nuevoComent = valoracio.comentari
                                    nuevoRating = valoracio.puntuacio
                                    showEditDialog = true
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        val ok = viewModel.eliminarValoracio(valoracio.id)
                                        if (ok) viewModel.get_informacio_ruta(ruta_id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            if (showEditDialog && valoracioSeleccionada != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Editar valoració") },
                    text = {
                        Column {
                            Row {
                                repeat(5) { i ->
                                    IconButton(onClick = { nuevoRating = (i + 1).toFloat() }) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Estrella",
                                            tint = if ((i + 1) <= nuevoRating) Color.Yellow else Color.Gray
                                        )
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = nuevoComent,
                                onValueChange = { nuevoComent = it },
                                label = { Text("Comentari") }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                valoracioSeleccionada?.let {
                                    viewModel.editarValoracio(
                                        valoracioSeleccionada!!,
                                        nuevoComent,
                                        nuevoRating.toFloat(),

                                        onSuccess = {
                                            showEditDialog = false
                                            viewModel.get_informacio_ruta(ruta_id)
                                        },
                                        onError = {
                                            println("Error editant valoració: $it")
                                            showEditDialog = false
                                        })
                                        showEditDialog = false
                                    }
                                }
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel·lar")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showRatingDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07F285))
            ) {
                Text(text = (getString(context, R.string.auval, selectedLanguage)), color = Color.Black)
            }
        }


    }
}

