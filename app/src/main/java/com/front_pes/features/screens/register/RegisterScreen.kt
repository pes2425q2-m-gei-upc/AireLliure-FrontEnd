package com.front_pes.features.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString

const val RegisterScreenDestination = "Register"

@Composable
fun RegisterScreen(viewModel: RegisterViewModel = viewModel(), title: String, onNavigateToMap: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat_password by remember { mutableStateOf("") }
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Logo (Reemplazar por tu recurso local si tienes uno)
        Image(
            painter = painterResource(id = R.drawable.airelliurelogo), // Cambia 'logo' por el nombre de tu recurso
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )

        Text(text = getString(context, R.string.signup, selectedLanguage), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Correu") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(

            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            label = { Text(text = getString(context, R.string.username, selectedLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text(text = getString(context, R.string.password, selectedLanguage)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {viewModel.register {
                onNavigateToMap()  // Navega si el register es exitoso
            }},
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF05C7F2),
                            Color(0xFF07F285)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = getString(context, R.string.SIGNUP, selectedLanguage), color = Color.White)
        }

        if (viewModel.errorMessage != null) {
            Text(viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
        }
    }
}
