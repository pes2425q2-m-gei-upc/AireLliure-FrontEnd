package com.front_pes.features.screens.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SettingsScreen(onNavigateToLogin : () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Español", "Català", "English")
    var selectedOption by remember { mutableStateOf(options[0]) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)) // Light gray background
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings Icon",
            tint = Color.Black,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Settings", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // White container with rounded edges
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp), // Rounded edges
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Language:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Button(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .menuAnchor()
                                .width(100.dp) // Cambia el ancho del botón
                                .height(35.dp) // Cambia la altura del botón
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF05C7F2),
                                            Color(0xFF07F285)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)

                                )
                                .padding(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(selectedOption)

                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(100.dp) // Ajusta el ancho del menú
                                .offset(x = (10).dp, y = 5.dp)
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(75.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Centra los elementos en la fila
                ) {
                    Button(
                        onClick = {
                            updateUserStatus(context, newEstat = "offline") { response ->
                                onNavigateToLogin()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Centra los elementos en la fila
                ) {
                    Button(
                        onClick = {onNavigateToLogin()},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Delete User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
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
