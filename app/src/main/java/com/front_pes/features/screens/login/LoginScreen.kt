package com.front_pes.features.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R

const val LoginScreenDestination = "Login"

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit)
{

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Logo (Reemplazar por tu recurso local si tienes uno)
        Image(
            painter = painterResource(id = R.drawable.airelliurelogo), // Cambia 'logo' por el nombre de tu recurso
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )

        Text("Sign In", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        //De momento este boton navega a la pantalla de map
        Button(
            onClick = {
                viewModel.login {
                    onNavigateToMap()  // Navega si el login es exitoso
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(8.dp))
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
            Text("SIGN IN", color = Color.White)
        }

        // Mostrar mensaje de error si hay un fallo en el login
        if (viewModel.errorMessage != null) {
            Text(viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
        }


        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(0xFF3B5998)),
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .weight(0.7f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.fb36), // Reemplaza por tu recurso de logo de Google
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = "Facebook", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .weight(0.7f),
                colors = ButtonDefaults.buttonColors(Color(0xFFFDFDFD)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.goog), // Reemplaza por tu recurso de logo de Google
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Google", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("If you don't have an account")
        Spacer(modifier = Modifier.height(10.dp))

        //Aquest boto et porta cap a la pantalla de Register
        Button(
            onClick = {onNavigateToRegister()},
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(8.dp))
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
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("SIGN UP", color = Color.White)
        }
    }
}
