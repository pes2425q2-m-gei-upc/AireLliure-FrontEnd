package com.front_pes.features.screens

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.map.MapScreen
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.settings.SettingsScreen
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.xats.ChatListScreen
import com.front_pes.features.screens.xamistat.LlistatAmistatScreen
import com.front_pes.features.screens.xamistat.DetallAmistatScreen
import com.front_pes.getString
import kotlinx.coroutines.launch
import java.util.Locale

import com.front_pes.utils.SelectorIndex
import com.front_pes.SelectedContaminants

const val MainScreenDestination = "Main"

@Composable
fun ContentScreen(
    modifier: Modifier,
    selectedIndex: Int = 1,
    reloadMap: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreateChat: () -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToChat: (Int, String) -> Unit,
    onNavigateToGroupDetail: (Int) -> Unit
)

 {
    val context = LocalContext.current
    var selectedAmistat by remember { mutableStateOf<String>("") }
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language)}
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    when (selectedIndex) {
        0 -> UserPageScreen(title = getString(context, R.string.username, currentLocale), onNavigateToLogin = onNavigateToLogin)
        1 -> MapScreen(title = getString(context, R.string.map, currentLocale), reloadTrigger = reloadMap)
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
                    }
                )
            } else {
                DetallAmistatScreen(
                    userId = selectedAmistat,
                    onBack = { selectedAmistat = "" }
                )
            }
        }
    }
}

@Composable
fun DrawerContent(selectedIndex: Int, onItemClicked: (Int) -> Unit) {
    val context = LocalContext.current
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val drawerItems = listOf(
        getString(context, R.string.profile, selectedLanguage) to Icons.Default.Person,
        getString(context, R.string.map, selectedLanguage) to Icons.Default.LocationOn,
        getString(context, R.string.settings, selectedLanguage) to Icons.Default.Settings,
        getString(context, R.string.chats, selectedLanguage) to Icons.Default.Email,
        getString(context, R.string.friends, selectedLanguage) to Icons.Default.Face
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

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

    val navItemList = listOf(
        NavItem(getString(context, R.string.airQ, selectedLanguage), Icons.Default.Person),
        NavItem(getString(context, R.string.routes, selectedLanguage), Icons.Default.LocationOn),
    )

    var reloadMap by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(selectedIndex) }
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

            floatingActionButton = {
                var showFilterDialog by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .padding(bottom = 8.dp, end = 8.dp)
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(28.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }

                if (showFilterDialog) {
                    FilterDialog(onDismiss = {
                        reloadMap = !reloadMap
                        showFilterDialog = false
                    })
                }
            },

            bottomBar = {
                if (!hideBars) {
                    NavigationBar {
                        navItemList.forEachIndexed { index, navItem ->
                            val isSelected = SelectorIndex.selectedIndex == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    SelectorIndex.selectedIndex = if (isSelected) -1 else index
                                },
                                icon = {
                                    Icon(
                                        imageVector = navItem.icon,
                                        contentDescription = "Icon",
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                label = {
                                    Text(
                                        text = navItem.label,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
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
                reloadMap = reloadMap
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

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por contaminantes", fontWeight = FontWeight.Bold) },
        text = {
            Column (
                verticalArrangement = Arrangement.spacedBy((-5).dp)
            )
            {
                contaminantes.forEachIndexed { index, contaminante ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text("Quitar filtros")
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
                    Text("Cerrar")
                }
            }
        }
    )
}
