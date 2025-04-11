package com.front_pes.features.screens.xamistat


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.front_pes.features.screens.xats.XatViewModel

const val LlistatAmistatScreen = "AmistatListScreen"
enum class Selector{
    AMISTATS,
    USUARIS
}


@Composable
fun LlistatAmistatScreen(onAmistatClick: (String) -> Unit, viewModel: LlistatAmistatViewModel = viewModel()) {

    var currentMode by remember {mutableStateOf(Selector.AMISTATS)}

    LaunchedEffect(Unit) { viewModel.getXatsAmics()}
    LaunchedEffect(Unit) { viewModel.get_usuaris() }
    val amistatList = viewModel.llista_amics
    val usuarisList = viewModel.all_users
    Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp)) {
        Text(
            text = "Amistats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (currentMode == Selector.AMISTATS) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.clickable { currentMode = Selector.AMISTATS }
        )

        Text(
            text = "Usuaris",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (currentMode == Selector.USUARIS) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.clickable { currentMode = Selector.USUARIS }
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 15.dp)) {

            if(currentMode == Selector.AMISTATS){
                items(amistatList) {item ->
                    AmistatListItem(name = item.nom, onClick = {onAmistatClick(item.id)})
                }
            } else {
                items(usuarisList){
                    user -> UsuariListItem(name = user.nom)
                }
            }
        }
    }
}

@Composable
fun AmistatListItem(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UsuariListItem(name: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Text(
                text = name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
