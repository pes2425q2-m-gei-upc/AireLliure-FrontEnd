package com.front_pes.features.screens

import com.front_pes.features.screens.xats.ChatListScreen
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
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.Ranking.RankingScreen
import com.front_pes.features.screens.administrador.HabilitacionsScreen
import com.front_pes.features.screens.login.LoginScreenDestination
import com.front_pes.features.screens.map.MapScreen
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.settings.SettingsScreen
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.xamistat.BloqueigScreen
import com.front_pes.features.screens.xamistat.LlistatAmistatScreen
import com.front_pes.features.screens.xamistat.DetallAmistatScreen
import com.front_pes.getString
import kotlinx.coroutines.launch
import java.util.Locale

import com.front_pes.utils.SelectorIndex


const val MainScreenDestination = "Main"


@Composable
fun ContentScreen(modifier: Modifier, selectedIndex: Int, onNavigateToLogin: () -> Unit, onChangeIndex: (Int) -> Unit ) {
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
        1 -> MapScreen(title = getString(context, R.string.map, currentLocale),)
        2 -> SettingsScreen(onNavigateToLogin = onNavigateToLogin)
        3 -> ChatListScreen(onChatClick = { chatName ->
            Log.d("ChatList", "Has fet clic a $chatName") })
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
        7 -> {
            if (CurrentUser.administrador) {
                HabilitacionsScreen(onNavigateToBlocks = { onChangeIndex(7) })
            }
        }
    }
}

@Composable
fun DrawerContent(selectedIndex: Int, onItemClicked: (Int) -> Unit) {
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val drawerItems = buildList {
        add(getString(context, R.string.profile, selectedLanguage) to Icons.Default.Person)
        add(getString(context, R.string.map, selectedLanguage) to Icons.Default.LocationOn)
        add(getString(context, R.string.settings, selectedLanguage) to Icons.Default.Settings)
        add(getString(context, R.string.chats, selectedLanguage) to Icons.Default.Email)
        add(getString(context, R.string.friends, selectedLanguage) to Icons.Default.Face)
        add(getString(context, R.string.ranking, selectedLanguage) to Icons.Default.Info)
        if (CurrentUser.administrador) {
            add(getString(context, R.string.admin, selectedLanguage) to Icons.Default.Warning)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centra todo el contenido

    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_user), // Usa una imagen aquí
            contentDescription = "User Image",
            modifier = Modifier
                .size(100.dp) // Tamaño más grande
                .padding(bottom = 8.dp),
            tint = Color.Unspecified
        )
        Text(CurrentUser.nom, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(CurrentUser.correu, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(16.dp))

        drawerItems.forEachIndexed { index, (label, icon) ->
            DrawerItem(text = label, icon = icon, selected = selectedIndex == index) {
                onItemClicked(index) // Cambia la pantalla y cierra el Drawer
            }
        }
    }
}

@Composable
fun DrawerItem(text: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(if (selected) Color(0xFF6B6B6B) else Color.Transparent, shape = RoundedCornerShape(12.dp))
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
            placeholder = { Text("Buscar", color = Color.Gray) },
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
fun MainScreen(modifier: Modifier = Modifier, title: String, onNavigateToLogin: () -> Unit) {

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    val navItemListMap = listOf(
        NavItem(getString(context, R.string.airQ, selectedLanguage), Icons.Default.Person),
        NavItem(getString(context, R.string.routes, selectedLanguage), Icons.Default.LocationOn)
    )

    val navItemListAmistat = listOf(
        NavItem(getString(context, R.string.Relacions, selectedLanguage), Icons.Default.Share),
        NavItem(getString(context, R.string.Block, selectedLanguage), Icons.Default.Lock)
    )

    var selectedIndex by remember { mutableIntStateOf(1) }
    var mapFilterIndex by remember { mutableIntStateOf(0) } // 0: Calidad aire, 1: Rutas

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val hideBars = selectedIndex == 0 || selectedIndex == 2

    BackHandler {
        selectedIndex = 1
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                selectedIndex = selectedIndex,
                onItemClicked = { index ->
                    selectedIndex = index
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 32.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
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
            },
            bottomBar = {
                if (!hideBars) {
                    NavigationBar {
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

                            // 🔧 Esta parte está fuera del NavigationBarItem (¡clave!)
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
                                label = labelContent
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
                onChangeIndex = { selectedIndex = it }
            )
        }
    }
}
