package com.front_pes.features.screens.user

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.network.RetrofitClient
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import coil.compose.AsyncImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

const val UserPageScreenDestination = "UserPage"

@Composable
fun FotoUsuari(url: String?) {
    AsyncImage(
        model = url ?: "", // por si es null
        contentDescription = "user picture",
        modifier = Modifier
            .size(175.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // imagen por defecto mientras carga
        error = painterResource(R.drawable.ic_user)         // imagen por defecto si falla
    )
}

@Composable
fun EditProfileDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit, onUploadImage: (Uri) -> Unit) {
    var newName by remember { mutableStateOf(CurrentUser.nom) }
    var newAbout by remember { mutableStateOf(CurrentUser.about) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_p)) },
        text = {
            Column {
                OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text(text = getString(context, R.string.username, selectedLanguage)) })
                OutlinedTextField(value = newAbout, onValueChange = { newAbout = it }, label = { Text(text = getString(context, R.string.about, selectedLanguage)) })
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar imatge")
                }

                selectedImageUri?.let {
                    Text("Imatge seleccionada: ${it.lastPathSegment}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onUploadImage(it) }) {
                        Text("Pujar imatge")
                    }
                }
            }
        },

        confirmButton = {
            Button(onClick = { onSave(newName, newAbout) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun updateUserProfile(
    context: android.content.Context,
    newName: String? = null,
    newAbout: String? = null,
    newStatus: String? = null,
    onSuccess: (UpdateProfileResponse) -> Unit)
{
    val request = UpdateProfileRequest(nom = newName, about = newAbout, estat = newStatus)
    val call = RetrofitClient.apiService.updateProfile(CurrentUser.correu, request)

    call.enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    CurrentUser.nom = updatedUser.nom
                    CurrentUser.about = updatedUser.about
                    onSuccess(updatedUser)
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        }
    })
}

fun uploadProfileImage(
    context: Context,
    imageUri: Uri,
    onSuccess: (UpdateProfileResponse) -> Unit,
    onFailure: () -> Unit
) {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri)
    val requestBody = inputStream?.readBytes()?.toRequestBody("image/*".toMediaTypeOrNull())
    inputStream?.close()

    if (requestBody != null) {
        val imagePart = MultipartBody.Part.createFormData(
            name = "imatge",
            filename = "profile.jpg",
            body = requestBody
        )

        RetrofitClient.apiService.updateProfileImage(CurrentUser.correu, imagePart)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val updatedUser = response.body()!!
                        CurrentUser.imatge = updatedUser.imatge
                        onSuccess(updatedUser)
                        Toast.makeText(context, "Image updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show()
                    onFailure()
                }
            })
    } else {
        Toast.makeText(context, "Could not read image file", Toast.LENGTH_SHORT).show()
        onFailure()
    }
}

@Composable
fun UserPageScreen (title: String, onNavigateToLogin : () -> Unit) {

    val punts = CurrentUser.punts;
    val correu = CurrentUser.correu;
    val estat = CurrentUser.estat;
    val admin = CurrentUser.administrador;

    var nom by remember { mutableStateOf(CurrentUser.nom) }
    var about by remember { mutableStateOf(CurrentUser.about) }
    var image by remember { mutableStateOf(CurrentUser.imatge)}

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language)}
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            FotoUsuari(url = CurrentUser.imatge)

            Text(nom, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(correu, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = getString(context, R.string.friends, selectedLanguage), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "0",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ) //Crida per a saber les persones que tenim afegides com amics
                }

                Spacer(modifier = Modifier.width(30.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = getString(context, R.string.points, selectedLanguage), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${punts}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { showDialog = true },
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
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = getString(context, R.string.edit_p, selectedLanguage),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (showDialog) {
                EditProfileDialog(
                    onDismiss = { showDialog = false },
                    onSave = { newName, newAbout, ->
                        updateUserProfile(context, newName, newAbout) { updatedUser ->
                            nom = updatedUser.nom
                            about = updatedUser.about
                            showDialog = false
                        }
                        showDialog = false
                    },
                    onUploadImage = { uri ->
                        uploadProfileImage(
                            context = context,
                            imageUri = uri,
                            onSuccess = { updatedUser ->
                                image = updatedUser.imatge
                            },
                            onFailure = {}
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = getString(context, R.string.user_info, selectedLanguage), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
                //colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = getString(context, R.string.about, selectedLanguage) + ": ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = about,
                            fontSize = 15.sp
                        )
                    }
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getString(context, R.string.state, selectedLanguage) + ": ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                    if (estat == "actiu") {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF00C853),
                                    shape = RoundedCornerShape(12.dp)
                                ) // Color de fondo
                                .padding(4.dp), // Espaciado interno para el texto
                        ) {
                            Text(
                                text = getString(context, R.string.online, selectedLanguage),  //HA DE CONCORDAR AMB EL TIPUS D'USUARI: ADMINISTRADOR O NORMAL
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                ) // Color de fondo
                                .padding(4.dp), // Espaciado interno para el texto
                        ) {
                            Text(
                                text = getString(context, R.string.offline, selectedLanguage),  //HA DE CONCORDAR AMB EL TIPUS D'USUARI: ADMINISTRADOR O NORMAL
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = getString(context, R.string.ratings, selectedLanguage) + ": ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "0",  //HA DE CONCORDAR AMBM EL NOMBRE DE VALORACIONS POSADES PER L'USUARI
                        fontSize = 15.sp
                    )
                }
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getString(context, R.string.usertype, selectedLanguage) + ": ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    val userTypeText = if (admin) getString(context, R.string.admin, selectedLanguage)
                    else getString(context, R.string.user, selectedLanguage)

                    val userTypeColor = if (admin) Color(0xFFFFC107) else Color(0xFF00C853)

                    Box(
                        modifier = Modifier
                            .background(
                                userTypeColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp),
                    ) {
                        Text(
                            text = userTypeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                }
            }
        }
    }
}}
