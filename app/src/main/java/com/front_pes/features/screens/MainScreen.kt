package com.front_pes.features.screens

import android.content.Intent
import android.net.Uri
import com.front_pes.features.screens.xats.ChatListScreen
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
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
import androidx.compose.material.icons.filled.ThumbUp

import com.front_pes.features.screens.ActivitatsEvents.EventScreen
import com.front_pes.features.screens.ActivitatsEvents.eventScreen
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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.Modifier
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.NavigationBarItemDefaults


import com.front_pes.getString
import kotlinx.coroutines.launch
import java.util.Locale

import com.front_pes.utils.SelectorIndex
import com.front_pes.SelectedContaminants

import com.front_pes.features.screens.map.RutasDetailScreen
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.MapViewModel
import com.front_pes.features.screens.map.RutaAmbPunt
import com.front_pes.ui.theme.LocalCustomColors

const val MainScreenDestination = "Main"

@Composable
fun FotoUsuari(url: String?) {
    AsyncImage(
        model = url ?: "", // por si es null
        contentDescription = "user picture",
        modifier = Modifier
            .size(100.dp)
            .padding(bottom = 8.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // imagen por defecto mientras carga
        error = painterResource(R.drawable.ic_user)         // imagen por defecto si falla
    )
}

@Composable
fun ContentScreen(
    modifier: Modifier,
    selectedIndex: Int = 1,
    reloadMap: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreateChat: () -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToChat: (Int, String) -> Unit,
    onNavigateToGroupDetail: (Int) -> Unit,
    onChangeIndex: (Int) -> Unit,
    selectedRutaInt: Int?,
    onRutaSelected: (Int) -> Unit,
    onRutaBack: () -> Unit
)

 {
    val context = LocalContext.current
    var selectedAmistat by remember { mutableStateOf<String>("") }
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language)}
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    SideEffect {
        SelectorIndex.selectedIndex = selectedIndex
    }
    when (selectedIndex) {
        0 -> UserPageScreen(title = getString(context, R.string.username, currentLocale), onNavigateToLogin = onNavigateToLogin)
        1 -> {
            if(selectedRutaInt == null) {
                MapScreen(
                    title = getString(context, R.string.map, currentLocale),
                    reloadTrigger = reloadMap,
                            onRutaClick = { rutaID ->
                                onRutaSelected(rutaID)
                    },
                )
            }
            else {
                selectedRutaInt?.let { rutaId ->
                    RutasDetailScreen(
                        onBack = { onRutaBack() },
                        ruta_id = rutaId
                    )
                }
                }
        }
        2 -> SettingsScreen(onNavigateToLogin = onNavigateToLogin)
        3 -> ChatListScreen(
            onChatClick = { chatId, userName ->
                onNavigateToChat(chatId, userName)
            },
            onNovaConversacioClick = onNavigateToCreateChat,
            onCrearGrupClick = onNavigateToCreateGroup
        )

        4 -> {
            if(selectedAmistat == ""){
                LlistatAmistatScreen(
                    onAmistatClick = { amistatID ->
                        selectedAmistat = amistatID
                    },
                    onNavigateToBlocks = {onChangeIndex(6)},
                )
            } else {
                DetallAmistatScreen(
                    userId = selectedAmistat,
                    onBack = { selectedAmistat = "" }
                )
            }
        }
        5-> RankingScreen(onChatClick = { chatName ->
            Log.d("ChatList", "Has fet clic a $chatName") })
        6-> BloqueigScreen(
            onNavigateToRelations={onChangeIndex(4)})
        7-> HabilitacionsScreen()
        8 -> EventScreen()
    }
}

@Composable
fun DrawerContent(selectedIndex: Int, onItemClicked: (Int) -> Unit) {
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val baseDrawerItems = listOf(
        0 to (getString(context, R.string.profile, selectedLanguage) to Icons.Default.Person),
        1 to (getString(context, R.string.map, selectedLanguage) to Icons.Default.LocationOn),
        2 to (getString(context, R.string.settings, selectedLanguage) to Icons.Default.Settings),
        3 to (getString(context, R.string.chats, selectedLanguage) to Icons.Default.Email),
        4 to (getString(context, R.string.friends, selectedLanguage) to Icons.Default.Face),
        5 to (getString(context, R.string.ranking, selectedLanguage) to Icons.Default.Info),
        6 to (getString(context, R.string.calendar, selectedLanguage) to Icons.Default.Info),
        8 to (getString(context, R.string.event_identif, selectedLanguage) to Icons.Default.ThumbUp)
    )

    val adminDrawerItems = if (CurrentUser.administrador) {
        listOf(7 to (getString(context, R.string.admin, selectedLanguage) to Icons.Default.Warning))
    } else {

        emptyList()
    }

    val drawerItems = baseDrawerItems + adminDrawerItems

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centra todo el contenido

    ) {
        Spacer(modifier = Modifier.height(25.dp))
        if(CurrentUser.imatge != null)FotoUsuari(url = CurrentUser.imatge)
        else{
            Image(
                painter = painterResource(id = R.drawable.ic_user), //para que ésto os funcione, poned el nombre de una foto que metáis en res/drawable, una vez conectemos back y front convertiré éste composable para que use API para obtener los valores
                contentDescription = "user picture",
                modifier = Modifier
                    .size(175.dp)
                    .clip(CircleShape)
            )
        }
        Text(CurrentUser.nom, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(CurrentUser.correu, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(16.dp))

        drawerItems.forEach { (actualIndex, item) ->
            val (label, icon) = item
            DrawerItem(
                text = label,
                icon = icon,
                selected = selectedIndex == actualIndex
            ) {
                onItemClicked(actualIndex)
            }
        }
    }
}

@Composable
fun DrawerItem(text: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val customColors = LocalCustomColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(if (selected) customColors.selectedItem else Color.Transparent, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var textSearch by remember { mutableStateOf("") }
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = textSearch,
            onValueChange = { textSearch = it },
            placeholder = { Text(text = (getString(context, R.string.buscar, selectedLanguage)), color = Color.Gray) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    title: String,
    selectedIndex: Int = 1,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreateChat: () -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToChat: (Int, String) -> Unit,
    onNavigateToGroupDetail: (Int) -> Unit
) {
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val mapViewModel: MapViewModel = viewModel()

    val estacions = remember { mutableStateListOf<EstacioQualitatAireResponse>() }
    val rutesAmbPunt = remember { mutableStateListOf<RutaAmbPunt>() }

    val navItemListMap = listOf(
        NavItem(getString(context, R.string.airQ, selectedLanguage), Icons.Default.Person),
        NavItem(getString(context, R.string.routes, selectedLanguage), Icons.Default.LocationOn)
    )

    val navItemListAmistat = listOf(
        NavItem(getString(context, R.string.Relacions, selectedLanguage), Icons.Default.Share),
        NavItem(getString(context, R.string.Block, selectedLanguage), Icons.Default.Lock)
    )

    var reloadMap by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(selectedIndex) }
    var selectedRutaInt by remember { mutableStateOf<Int?>(null) }
    var mapFilterIndex by remember { mutableIntStateOf(0) } // 0: Calidad aire, 1: Rutas

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val hideBars = selectedIndex == 0 || selectedIndex == 2 || selectedRutaInt != null

    // Load estacions and rutas
    LaunchedEffect(Unit) {
        mapViewModel.fetchEstacionsQualitatAire(
            onSuccess = {
                estacions.clear()
                estacions.addAll(it)
            },
            onError = { Log.e("MainScreen", "Error cargando estaciones") }
        )

        mapViewModel.fetchRutes(
            onSuccess = { rutas ->
                rutas.forEach { ruta ->
                    ruta.punt_inici?.let { puntId ->
                        mapViewModel.fetchPuntByID(
                            pk = puntId,
                            onSuccess = { punt ->
                                rutesAmbPunt.add(RutaAmbPunt(ruta = ruta, punt = punt))
                            },
                            onError = { Log.e("MainScreen", "Error cargando punto: $it") }
                        )
                    }
                }
            },
            onError = { Log.e("MainScreen", "Error cargando rutas") }
        )
    }

    BackHandler {
        selectedIndex = 1
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                selectedIndex = selectedIndex,
                onItemClicked = { index ->
                    if (index == 6) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("content://com.android.calendar/time/")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } else {
                        selectedIndex = index
                    }
                    scope.launch { drawerState.close() }
                }

            )
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (selectedRutaInt == null) {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 40.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "User Profile",
                                modifier = Modifier.size(26.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },

            floatingActionButton = {
                if (selectedIndex == 1 && selectedRutaInt == null) {
                    var expanded by remember { mutableStateOf(false) }
                    var showFilterDialog by remember { mutableStateOf(false) }
                    var showPopup by remember { mutableStateOf(false) }
                    var selectedTabIndex by remember { mutableStateOf(0) }

                    val customColors = LocalCustomColors.current

                    val isTracking by remember { derivedStateOf { mapViewModel.isTracking } }
                    val totalDistance by remember { derivedStateOf { mapViewModel.totalDistance } }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 32.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        if (isTracking) {
                            Column(
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                val elapsedMillis = mapViewModel.elapsedTime

                                val elapsedFormatted = remember(elapsedMillis) {
                                    val totalSeconds = elapsedMillis / 1000
                                    val minutes = totalSeconds / 60
                                    val seconds = totalSeconds % 60
                                    String.format("%02d:%02d", minutes, seconds)
                                }
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = elapsedFormatted,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            mapViewModel.stopTracking(context)
                                            mapViewModel.nomRutaRecorreguda = ""
                                            mapViewModel.totalDistance = 0f
                                            mapViewModel.targetDistance = 0f
                                        },
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(end = 8.dp)
                                    ) {
                                        Text("Detener")
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    val targetDistance = mapViewModel.targetDistance
                                    val rutaFinalitzada = mapViewModel.rutaFinalitzada

                                    Text(
                                        text = if (rutaFinalitzada) {
                                            "¡Ruta finalizada!"
                                        } else {
                                            "Ruta: ${String.format("%.2f", totalDistance / 1000)} km / " +
                                                    "${String.format("%.2f", targetDistance / 1000)} km"
                                        },
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .padding(end = 8.dp, bottom = 8.dp)
                        ) {
                            AnimatedVisibility(
                                visible = expanded,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        showFilterDialog = true
                                        expanded = false
                                    },
                                    containerColor = customColors.selectedItem
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Filtro", tint = Color.White)
                                }
                            }

                            AnimatedVisibility(
                                visible = expanded,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        showPopup = true
                                        expanded = false
                                    },
                                    containerColor = customColors.selectedItem
                                ) {
                                    Icon(Icons.Default.List, contentDescription = "Lista", tint = Color.White)
                                }
                            }

                            FloatingActionButton(
                                onClick = { expanded = !expanded },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                                    contentDescription = "Expandir menú",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Dialogo filtro
                        if (showFilterDialog) {
                            FilterDialog(onDismiss = {
                                reloadMap = !reloadMap
                                showFilterDialog = false
                            })
                        }

                        // Dialogo rutas/estaciones
                        if (showPopup) {
                            AlertDialog(
                                onDismissRequest = { showPopup = false },
                                confirmButton = {},
                                dismissButton = {},
                                text = {
                                    Column(modifier = Modifier.padding(8.dp)) {

                                        val tabTitles = listOf((getString(context, R.string.routes, selectedLanguage)),(getString(context, R.string.estaciones, selectedLanguage)))

                                        TabRow(
                                            selectedTabIndex = selectedTabIndex,
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.primary,
                                            indicator = {},
                                            divider = {}
                                        ) {
                                            tabTitles.forEachIndexed { index, title ->
                                                Tab(
                                                    selected = selectedTabIndex == index,
                                                    onClick = { selectedTabIndex = index },
                                                    selectedContentColor = Color.White,
                                                    unselectedContentColor = Color.Black
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(50),
                                                        color = if (selectedTabIndex == index)
                                                            MaterialTheme.colorScheme.primary
                                                        else
                                                            Color.LightGray,
                                                        shadowElevation = 2.dp
                                                    ) {
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier
                                                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                                                .widthIn(min = 100.dp) // 💡 Aumenta el ancho mínimo del botón
                                                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                                        ) {
                                                            Text(
                                                                text = title,
                                                                style = MaterialTheme.typography.labelLarge,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (selectedTabIndex == 0) {
                                            LazyColumn(
                                                modifier = Modifier
                                                    .height(300.dp)
                                                    .fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(rutesAmbPunt) { rutaAmbPunt ->
                                                    Text(
                                                        text = rutaAmbPunt.ruta.nom,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                SelectorIndex.selectedRuta = rutaAmbPunt
                                                                SelectorIndex.selectedEstacio = null
                                                                showPopup = false
                                                            }
                                                            .padding(16.dp),
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier
                                                    .height(300.dp)
                                                    .fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(estacions) { estacio ->
                                                    Text(
                                                        text = estacio.nom_estacio,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                SelectorIndex.selectedEstacio = estacio
                                                                SelectorIndex.selectedRuta = null
                                                                showPopup = false
                                                            }
                                                            .padding(16.dp),
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            ,

            bottomBar = {
                if (!hideBars) {
                    val customColors = LocalCustomColors.current
                    NavigationBar (
                        containerColor = customColors.bottomBar,

                    ) {
                        val navItemsToShow = when (selectedIndex) {
                            1 -> navItemListMap
                            4, 6 -> navItemListAmistat
                            else -> emptyList()
                        }

                        navItemsToShow.forEachIndexed { index, navItem ->
                            val actualIndex = when (selectedIndex) {
                                1 -> selectedIndex // Stay on same screen
                                4, 6 -> if (index == 0) 4 else 6
                                else -> index
                            }

                            val isSelected = when (selectedIndex) {
                                1 -> mapFilterIndex == index
                                else -> selectedIndex == actualIndex
                            }

                            val iconContent: @Composable () -> Unit = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = "Icon",
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            val labelContent: @Composable () -> Unit = {
                                Text(
                                    text = navItem.label,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (selectedIndex == 1) {
                                        mapFilterIndex = index
                                        SelectorIndex.selectedFiltre = if (isSelected) -1 else index
                                    } else {
                                        selectedIndex = actualIndex
                                    }
                                },
                                icon = iconContent,
                                label = labelContent,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = LocalCustomColors.current.selectedItem
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            ContentScreen(
                modifier = Modifier.padding(innerPadding),
                selectedIndex = selectedIndex,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToCreateChat = onNavigateToCreateChat,
                onNavigateToCreateGroup = onNavigateToCreateGroup,
                onNavigateToChat = onNavigateToChat,
                onNavigateToGroupDetail = onNavigateToGroupDetail,
                reloadMap = reloadMap,
                onChangeIndex = { selectedIndex = it },
                selectedRutaInt = selectedRutaInt,
                onRutaSelected = { selectedRutaInt = it },
                onRutaBack = { selectedRutaInt = null }
            )
        }
    }
}

@Composable
fun FilterDialog(onDismiss: () -> Unit) {
    val contaminantes = listOf(
        "NO2", "O3", "PM10", "H2S", "NO", "SO2",
        "PM2.5", "NOX", "CO", "C6H6", "PM1", "Hg"
    )

    val checkedStates = remember {
        mutableStateOf(
            contaminantes.map { it in SelectedContaminants.selected }
        )
    }
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text =(getString(context, R.string.f_p_cont, selectedLanguage)), fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy((-5).dp),
                modifier = Modifier
                    .heightIn(max = 350.dp)

            )
            {
                itemsIndexed(contaminantes) { index, contaminante ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val isChecked = !checkedStates.value[index]
                                checkedStates.value = checkedStates.value.toMutableList().also {
                                    it[index] = isChecked
                                }
                                if (isChecked) {
                                    SelectedContaminants.selected.add(contaminante)
                                    Log.d("FilterDialog", "Added: $contaminante, Selected=${SelectedContaminants.selected}")
                                } else {
                                    SelectedContaminants.selected.remove(contaminante)
                                    Log.d("FilterDialog", "Removed: $contaminante, Selected=${SelectedContaminants.selected}")
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedStates.value[index],
                            onCheckedChange = { isChecked ->
                                checkedStates.value = checkedStates.value.toMutableList().also {
                                    it[index] = isChecked
                                }
                                if (isChecked) {
                                    SelectedContaminants.selected.add(contaminante)
                                    Log.d("FilterDialog", "Added: $contaminante, Selected=${SelectedContaminants.selected}")
                                } else {
                                    SelectedContaminants.selected.remove(contaminante)
                                    Log.d("FilterDialog", "Removed: $contaminante, Selected=${SelectedContaminants.selected}")
                                }
                            }
                        )
                        Text(text = contaminante)
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {
                // Botón "Quitar filtros" a la izquierda
                Button(
                    onClick = {
                        SelectedContaminants.selected.clear()
                        checkedStates.value = List(contaminantes.size) { false }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = (getString(context, R.string.qfilt, selectedLanguage)))
                }

                // Botón "Cerrar" a la derecha
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = (getString(context, R.string.cerrar, selectedLanguage)))
                }
            }
        }
    )
}
