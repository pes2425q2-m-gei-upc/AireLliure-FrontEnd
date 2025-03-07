package com.front_pes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.front_pes.features.login.LoginScreen
import com.front_pes.features.login.LoginViewModel
import com.front_pes.features.map.MapViewModel
import com.front_pes.ui.theme.FRONTPESTheme
import com.front_pes.features.map.MapScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.front_pes.features.login.LoginScreenDestination
import com.front_pes.features.map.MapScreenDestination
import com.front_pes.features.register.RegisterScreen
import com.front_pes.features.register.RegisterScreenDestination


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
private fun AppNavigation() {
    //Objeto que se encarga de gestionar la navegacion
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginScreenDestination,)
    {
        composable(LoginScreenDestination){
            LoginScreen(
                onNavigateToMap = {
                    navController.navigate(MapScreenDestination)
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
    }
}
