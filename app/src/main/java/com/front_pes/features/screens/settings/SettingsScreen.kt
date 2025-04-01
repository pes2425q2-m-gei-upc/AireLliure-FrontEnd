package com.front_pes.features.screens.settings

import androidx.compose.foundation.Image
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.getString
import java.util.Locale
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import com.front_pes.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateToLogin : () -> Unit, languageViewModel: LanguageViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Español" to "es", "Català" to "ca", "English" to "en")
    //var selectedOption by remember { mutableStateOf(options[0]) }
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val selectedOption = options.find { it.second == selectedLanguage }?.first ?: "Español"
    val context = LocalContext.current
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language)}
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLanguage) {
        currentLocale = selectedLanguage
    }



    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete your account? This action is not reversible.") },
            confirmButton = {
                TextButton (onClick = {
                    showDialog = false
                    deleteUser(context) { onNavigateToLogin() }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings Icon",
            modifier = Modifier
                .size(100.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = getString(context, R.string.settings, currentLocale), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))

        // White container with rounded edges
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp) // Rounded edges
            //colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Text(
                        text = getString(context, R.string.language, currentLocale) + ":",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF05C7F2),
                            Color(0xFF07F285)
                        )
                    )

                    // Botón para Español
                    Button(
                        onClick = {
                            languageViewModel.changeLanguage("es")
                            },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05C7F2)),
                        enabled = selectedLanguage != "es"
                    ) {
                        Text("Español")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón para Catalán
                    Button(
                        onClick = {
                            languageViewModel.changeLanguage("ca")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05C7F2)),
                        enabled = selectedLanguage != "ca"
                    ) {
                        Text("Català")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón para Inglés
                    Button(
                        onClick = {
                            languageViewModel.changeLanguage("en")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05C7F2)),
                        enabled = selectedLanguage != "en"
                    ) {
                        Text("English")
                    }
                }

                Spacer(modifier = Modifier.height(75.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Centra los elementos en la fila
                ) {
                    Button(
                        onClick = {onNavigateToLogin()},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = getString(context, R.string.logout, currentLocale), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Centra los elementos en la fila
                ) {
                    Button(
                        onClick = {showDialog = true},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = getString(context, R.string.delete_user, currentLocale), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(text: String) { //A modificar para poder meter un icono/imagen especifica segun el item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF05C7F2), Color(0xFF07F285))
        )

        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Settings),
            contentDescription = "Settings Icon",
            tint = Color.White, // Ensures gradient applies correctly
            modifier = Modifier
                .size(24.dp)
                .background(brush = gradientBrush, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 18.sp)
    }
}

fun updateUserStatus(
    context: android.content.Context,
    newEstat: String,
    onSuccess: (UpdateProfileResponse) -> Unit
) {
    val request = UpdateProfileRequest(estat = newEstat)
    val call = RetrofitClient.apiService.updateProfile(CurrentUser.correu, request)

    call.enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(
            call: Call<UpdateProfileResponse>,
            response: Response<UpdateProfileResponse>
        ) {
            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    CurrentUser.estat = newEstat
                    onSuccess(updatedUser)
                    //Toast.makeText(context, "Logging out!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        }
    })
}

fun deleteUser(context: Context, onSuccess: () -> Unit) {
    val call: Call<Void> = RetrofitClient.apiService.deleteUser(CurrentUser.correu)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, "Error deleting account", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        }
    })
}