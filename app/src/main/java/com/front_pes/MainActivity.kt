package com.front_pes

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.front_pes.features.screens.login.LoginScreen
import com.front_pes.features.screens.map.MapViewModel
import com.front_pes.ui.theme.FRONTPESTheme
import com.front_pes.features.screens.map.MapScreen
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.front_pes.features.screens.login.LoginScreenDestination
import com.front_pes.features.screens.map.MapScreenDestination
import com.front_pes.features.screens.register.RegisterScreen
import com.front_pes.features.screens.xats.ChatListScreen
import com.front_pes.features.screens.register.RegisterScreenDestination
import com.front_pes.features.screens.MainScreen
import com.front_pes.features.screens.MainScreenDestination
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.settings.SettingsScreen
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.user.UserPageScreenDestination
import com.front_pes.features.screens.xats.ChatCreateScreen
import com.front_pes.features.screens.xats.ChatListScreenDestination
import com.front_pes.features.screens.xats.ChatScreen
import com.front_pes.features.screens.xats.ChatScreenDestination
import com.front_pes.features.screens.xats.GroupCreateScreen
import com.front_pes.features.screens.xats.GroupDetailScreen

import java.util.*



class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val languageViewModel: LanguageViewModel = viewModel()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

            // ✅ Forzar que TODA la app use `getString()`
            val currentLocale = remember { mutableStateOf(Locale.getDefault().language) }
            LaunchedEffect(selectedLanguage) {
                currentLocale.value = selectedLanguage
            }
                FRONTPESTheme {
                    AppNavigation(currentLocale.value)
                }
            }
        }

    override fun onStart() {
        super.onStart()
    }
}


//Esta funcion se encarga de toda la navegacion
@Composable
private fun AppNavigation(currentLocale: String) {
    //Objeto que se encarga de gestionar la navegacion
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = LoginScreenDestination
    )
    {
        composable(LoginScreenDestination){
            LoginScreen(
                title = getString(context, R.string.login, currentLocale),
                onNavigateToMap = {

                    navController.navigate(MainScreenDestination)
                    //navController.navigate(ChatListScreenDestination)
                },
                onNavigateToRegister = { navController.navigate(RegisterScreenDestination) }
            )
        }
        composable(MapScreenDestination){
            MapScreen(
                onRutaClick = {
                },
                title = getString(context, R.string.map, currentLocale)
            )
        }
        composable(RegisterScreenDestination){
            RegisterScreen(
                title = getString(context, R.string.signup, currentLocale),
                onNavigateToMap = {
                navController.navigate(MainScreenDestination)
            })
        }
        composable(UserPageScreenDestination) {
            UserPageScreen(
                title = getString(context, R.string.username, currentLocale),
                onNavigateToLogin = {
                    navController.navigate(LoginScreenDestination)
        }
            )
        }
        composable(
            "$MainScreenDestination?selectedTab={selectedTab}",
            arguments = listOf(
                navArgument("selectedTab") {
                    defaultValue = 1 // Pestaña por defecto: mapa
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val selectedTab = backStackEntry.arguments?.getInt("selectedTab") ?: 1
            MainScreen(
                title = getString(context, R.string.map, currentLocale),
                selectedIndex = selectedTab,
                onNavigateToLogin = {
                    navController.navigate(LoginScreenDestination)
                },
                onNavigateToCreateChat = {
                    navController.navigate("chat-create")
                },
                onNavigateToCreateGroup = {
                    navController.navigate("chat-group-create")
                },
                onNavigateToChat = { chatId, userName ->
                    navController.navigate("chat/$chatId/$userName")
                },
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate("group-detail/$groupId")
                }
            )
        }
        composable("main/chat") {
            MainScreen(
                title = getString(context, R.string.map, currentLocale),
                selectedIndex = 3, // índice de la pestaña de chats
                onNavigateToLogin = {
                    navController.navigate(LoginScreenDestination)
                },
                onNavigateToCreateChat = {
                    navController.navigate("chat-create")
                },
                onNavigateToCreateGroup = {
                    navController.navigate("chat-group-create")
                },
                onNavigateToChat = { chatId, userName ->
                    navController.navigate("chat/$chatId/$userName")
                },
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate("group-detail/$groupId")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginScreenDestination)
                },
                languageViewModel = viewModel()
            )
        }
        composable(ChatListScreenDestination) {
            ChatListScreen(
                onChatClick = { chatId, userName ->
                    navController.navigate("chat/$chatId/$userName")
                },
                onNovaConversacioClick = {
                    navController.navigate("chat-create")
                },
                onCrearGrupClick = {
                    navController.navigate("chat-group-create")
                }
            )
        }

        composable("chat/{chatId}/{userName}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")?.toIntOrNull()
            val userName = backStackEntry.arguments?.getString("userName")
            if (chatId != null && userName != null) {
                ChatScreen(chatId = chatId, userName = userName,
                    onBack = {
                        navController.navigate("$MainScreenDestination?selectedTab=3") {
                            popUpTo(ChatScreenDestination) { inclusive = true }
                        }
                    },
                    onNavigateToGroupDetail = { groupId ->
                    navController.navigate("group-detail/$groupId")
                })
            }
        }
        composable("chat-create") {
            ChatCreateScreen(
                onChatCreated = { chatId, userName ->
                    navController.navigate("chat/$chatId/$userName")
                },
                onBack = {
                    navController.navigate(ChatListScreenDestination) {
                        popUpTo("chat-create") { inclusive = true }
                    }
                }

            )
        }
        composable("chat-group-create") {
            GroupCreateScreen(
                onGroupCreated = { chatId, groupName ->
                    navController.navigate("chat/$chatId/$groupName")
                },
                onBack = {
                    navController.navigate(ChatListScreenDestination) {
                        popUpTo("chat-group-create") { inclusive = true }
                    }
                }
            )
        }
        composable("group-detail/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                GroupDetailScreen(
                    groupId = groupId,
                    onBack = {
                        navController.navigate("main/chat") {
                            popUpTo("group-detail/$groupId") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

fun getString (context: Context, resId: Int, locale: String): String{
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(locale))
    return context.createConfigurationContext(config).resources.getString(resId)
}
