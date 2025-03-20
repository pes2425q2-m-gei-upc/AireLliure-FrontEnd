package com.front_pes.features.screens.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R
import com.front_pes.getString
import java.util.Locale

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

    LaunchedEffect(selectedLanguage) {
        currentLocale = selectedLanguage
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)) // Light gray background
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            painter = painterResource(id = R.drawable.rueda), //para que ésto os funcione, poned el nombre de una foto que metáis en res/drawable, una vez conectemos back y front convertiré éste composable para que use API para obtener los valores
            contentDescription = "settings",
            modifier = Modifier
                .size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = getString(context, R.string.settings, currentLocale), fontSize = 26.sp, fontWeight = FontWeight.Bold)
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
                        onClick = {onNavigateToLogin()},
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