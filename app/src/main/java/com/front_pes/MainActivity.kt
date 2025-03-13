package com.front_pes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.front_pes.features.screens.register.RegisterScreenDestination
import com.front_pes.features.screens.MainScreen
import com.front_pes.features.screens.MainScreenDestination
import com.front_pes.features.screens.login.LoginViewModel
import com.front_pes.features.screens.user.UserPageScreen
import com.front_pes.features.screens.user.UserPageScreenDestination


class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FRONTPESTheme {
                AppNavigation()
            }
        }
    }
}

//Esta funcion se encarga de toda la navegacion
@Composable
private fun AppNavigation(modifer: Modifier = Modifier) {
    //Objeto que se encarga de gestionar la navegacion
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginScreenDestination
    )
    {
        composable(LoginScreenDestination){
            LoginScreen(
                onNavigateToMap = {
                    navController.navigate(MainScreenDestination)
                },
                onNavigateToRegister = { navController.navigate(RegisterScreenDestination) }
            )
        }
        composable(MapScreenDestination){
            MapScreen()
        }
        composable(RegisterScreenDestination){
            RegisterScreen()
        }
        composable(UserPageScreenDestination) {
            UserPageScreen()
        }
        composable(MainScreenDestination) {
            MainScreen()
        }
    }
}
