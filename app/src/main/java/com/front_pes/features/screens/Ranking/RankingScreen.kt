package com.front_pes.features.screens.Ranking

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.front_pes.features.screens.xats.XatViewModel

const val RankingScreen = "Ranking"

enum class Selector {
    ALL,
    AMICS
}

@Composable
fun RankingScreen(onChatClick: (Int) -> Unit, viewModel: RankingViewModel = viewModel()){

    LaunchedEffect(Unit) {
        viewModel.ranking_tt_users()
        viewModel.ranking_n_amics()
    }
    val usuaris_all = viewModel.ranking_all_users
    val usuaris_amic = viewModel.ranking_amics
    var currentMode by remember { mutableStateOf(Selector.ALL) }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 80.dp, start = 10.dp, end = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ranking Global",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.ALL) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.ALL }
                    .padding(10.dp)
            )

            Text(
                text = "Ranking Amics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.AMICS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.AMICS }
                    .padding(10.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 155.dp)
        ) {
            if (currentMode == Selector.ALL) {
                itemsIndexed(usuaris_all) { index, user ->
                    RankingListItem(
                        position = index + 1,
                        nom = user.name,
                        punts = user.points
                    )
                }
            } else {
                itemsIndexed(usuaris_amic) { index, user ->
                    RankingListItem(
                        position = index + 1,
                        nom = user.name,
                        punts = user.points
                    )
                }
            }
        }
    }
}

@Composable
fun RankingListItem(position: Int, nom: String, punts: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$position.",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.width(32.dp)
            )

            Text(
                text = nom,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$punts pts",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

