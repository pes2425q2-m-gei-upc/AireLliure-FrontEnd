package com.front_pes.features.screens.ActivitatsEvents

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedTextField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.Ranking.RankingScreen
import com.front_pes.features.screens.login.LoginScreenDestination
import com.front_pes.features.screens.map.MapScreen
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.settings.SettingsScreen
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.xamistat.BloqueigScreen
import com.front_pes.features.screens.xats.ChatListScreen
import com.front_pes.features.screens.xamistat.LlistatAmistatScreen
import com.front_pes.features.screens.xamistat.DetallAmistatScreen
import com.front_pes.features.screens.administrador.HabilitacionsScreen

import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.ui.unit.dp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.text.input.KeyboardType


import com.front_pes.getString
import kotlinx.coroutines.launch

import com.front_pes.utils.SelectorIndex
import com.front_pes.SelectedContaminants
import com.front_pes.features.screens.ActivitatsEvents.EventScreen
import com.front_pes.features.screens.ActivitatsEvents.eventScreen
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.MapViewModel
import com.front_pes.features.screens.map.RutaAmbPunt
import com.front_pes.ui.theme.LocalCustomColors


@Composable
fun FormCreacioScreen(viewModel: eventViewModel = viewModel()) {

    val context = LocalContext.current
    var nom by remember { mutableStateOf("") }
    var descripcio by remember { mutableStateOf("") }
    var dataInici by remember { mutableStateOf("") }
    var dataFi by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language)}

    // DatePickers
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

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text(text = getString(context, R.string.nom, selectedLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcio,
            onValueChange = { descripcio = it },
            label = { Text(text = getString(context, R.string.desc, selectedLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = dataInici,
                onValueChange = {},
                label = { Text(text = getString(context, R.string.dat_ini, selectedLanguage)) },
                modifier = Modifier.weight(1f).clickable {
                    showDatePicker { selected -> dataInici = selected }
                },
                enabled = false
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = dataFi,
                onValueChange = {},
                label = { Text(text = getString(context, R.string.dat_fi, selectedLanguage)) },
                modifier = Modifier.weight(1f).clickable {
                    showDatePicker { selected -> dataFi = selected }
                },
                enabled = false
            )
        }

        OutlinedTextField(
            value = limit,
            onValueChange = { limit = it },
            label = { Text(text = getString(context, R.string.lim_pers, selectedLanguage)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = getString(context, R.string.subim, selectedLanguage))
        }

        imageUri?.let {
            Text(text = getString(context, R.string.imsel, selectedLanguage)+": ${it.lastPathSegment}", fontSize = 12.sp)
        }

        Button(
            onClick = {
                viewModel.crear_activitat_event_public(nom, descripcio, dataInici, dataFi, limit.toInt())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = getString(context, R.string.creact, selectedLanguage))
        }
    }
}