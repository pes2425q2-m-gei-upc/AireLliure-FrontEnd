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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.front_pes.features.screens.login.LoginScreen
import com.front_pes.features.screens.map.MapViewModel
import com.front_pes.ui.theme.FRONTPESTheme
import com.front_pes.features.screens.map.MapScreen
import androidx.navigation.compose.composable
import com.front_pes.features.screens.login.LoginScreenDestination
import com.front_pes.features.screens.map.MapScreenDestination
import com.front_pes.features.screens.register.RegisterScreen
import ChatListScreen
import com.front_pes.features.screens.register.RegisterScreenDestination
import com.front_pes.features.screens.MainScreen
import com.front_pes.features.screens.MainScreenDestination
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.settings.SettingsScreen
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.user.UserPageScreenDestination
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
                },
                onNavigateToRegister = { navController.navigate(RegisterScreenDestination) }
            )
        }
        composable(MapScreenDestination){
            MapScreen(
                title = getString(context, R.string.map, currentLocale),
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
        composable(MainScreenDestination) {
            MainScreen(
                title = getString(context, R.string.map, currentLocale),
                onNavigateToLogin = {
                navController.navigate(LoginScreenDestination)
            })
        }
        composable("settings") {
            SettingsScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginScreenDestination)
                },
                languageViewModel = viewModel()
            )
        }
        composable("chats") {
            ChatListScreen(
                onChatClick = { chatName ->
                    // log.d per accedir a un xat específic.
                    Log.d("ChatList", "Clicked on chat with $chatName")
                }
            )
        }
    }
}

fun getString (context: Context, resId: Int, locale: String): String{
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(locale))
    return context.createConfigurationContext(config).resources.getString(resId)
}
