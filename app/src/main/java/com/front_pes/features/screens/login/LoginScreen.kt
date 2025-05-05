package com.front_pes.features.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

import com.front_pes.features.screens.settings.updateUserStatus
import androidx.compose.ui.platform.LocalContext

const val LoginScreenDestination = "Login"

@Composable
fun LoginScreen(
    title: String,
    viewModel: LoginViewModel = viewModel(),
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit)
{

    val isLoading by viewModel.isLoading.collectAsState()
    val token = "8587200690-n4o3qjmpcp8lemk9kgki9v8drpepmlb3.apps.googleusercontent.com"
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts
        .StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                onNavigateToMap()
            }
        }
        catch (ex: Exception) {
            Log.d("AireLliure", "GoogleSignIn ha fallat")
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
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
                modifier = Modifier.size(250.dp)
            )

            Text(text = getString(context, R.string.login, selectedLanguage), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text(text = getString(context, R.string.password, selectedLanguage)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }
            else {
                Button(
                    onClick = {
                        viewModel.login {
                            updateUserStatus(context, newEstat = "actiu") {
                                onNavigateToMap()
                            }
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
                    Text(text = getString(context, R.string.LOGIN, selectedLanguage), color = Color.White)
                }
            }

            // Mostrar mensaje de error si hay un fallo en el login
            if (viewModel.errorMessage != null) {
                Text(viewModel.errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        viewModel.signInWithGitHub(context,
                        onSuccess = {
                            // Navegar a la pantalla principal
                            onNavigateToMap()
                        },
                        onError = { errorMessage ->
                            // Mostrar mensaje de error
                            Log.e("GitHubLogin", errorMessage)
                        }
                    )},
                    colors = ButtonDefaults.buttonColors(Color(0xFF000000)),
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .weight(0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.git36), // Reemplaza por tu recurso de logo de Google
                            contentDescription = "Facebook",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(text = "GitHub", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = {
                        val opciones = GoogleSignInOptions.Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN
                        )
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                        val googleSignInCliente = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSignInCliente.signInIntent)
                    },
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

            Text(text = getString(context, R.string.no_ac, selectedLanguage), color = MaterialTheme.colorScheme.onSurface)

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
                Text(text = getString(context, R.string.SIGNUP, selectedLanguage), color = Color.White)
            }
        }
    }
}
