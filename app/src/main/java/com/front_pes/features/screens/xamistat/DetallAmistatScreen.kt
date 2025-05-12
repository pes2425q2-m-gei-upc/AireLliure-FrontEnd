package com.front_pes.features.screens.xamistat

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.front_pes.CurrentUser
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.features.screens.user.EditProfileDialog
import com.front_pes.features.screens.user.updateUserProfile
import com.front_pes.getString
import java.util.Locale


const val DetallAmistatScreen = "DetallAmistatListScreen"

@Composable
fun FotoUsuari(url: String?) {
    AsyncImage(
        model = url ?: "", // por si es null
        contentDescription = "user picture",
        modifier = Modifier
            .size(100.dp)
            .padding(bottom = 8.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // imagen por defecto mientras carga
        error = painterResource(R.drawable.ic_user)         // imagen por defecto si falla
    )
}


@Composable
fun DetallAmistatScreen(userId: String, onBack: () -> Unit, viewModel: DetallAmistatViewModel = viewModel()) {

    viewModel.getDetallAmic(userId)
    var user = viewModel.usuari
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language) }
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

            if(user?.imatge != null){
                com.front_pes.features.screens.FotoUsuari(url = user.imatge)
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.ic_user), //para que ésto os funcione, poned el nombre de una foto que metáis en res/drawable, una vez conectemos back y front convertiré éste composable para que use API para obtener los valores
                    contentDescription = "user picture",
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                )
            }
            Text(text = user?.nom ?: "", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = user?.correu ?: "", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

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
                        text = "${user?.punts}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Tornar enrere",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Button(
                onClick = {
                    viewModel.bloquejar_usuari()
                    onBack()
                },
                modifier = Modifier
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Block",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
