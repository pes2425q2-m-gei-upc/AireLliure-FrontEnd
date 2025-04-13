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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
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
    USUARIS,
    REBUDES,
    ENVIADES
}


@Composable
fun LlistatAmistatScreen(onAmistatClick: (String) -> Unit, viewModel: LlistatAmistatViewModel = viewModel()) {

    var currentMode by remember {mutableStateOf(Selector.AMISTATS)}
    val scrollState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.getXatsAmics()}
    LaunchedEffect(Unit) { viewModel.get_usuaris() }
    val amistatList = viewModel.llista_amics
    val usuarisList = viewModel.all_users
    val all_rebudes = viewModel.all_rebudes
    val all_enviades = viewModel.all_enviades
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 90.dp, start = 10.dp, end = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = "Amistats",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.AMISTATS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.AMISTATS }
                    .padding(10.dp)
            )

            Text(
                text = "Usuaris",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.USUARIS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.USUARIS }
                    .padding(10.dp)
            )
            Text(
                text = "Pendents",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.REBUDES) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.REBUDES }
                    .padding(10.dp)
            )
            Text(
                text = "Enviades",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.ENVIADES) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.ENVIADES }
                    .padding(10.dp)
            )
        }

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Cerca usuaris...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            if(currentMode == Selector.AMISTATS){
                items(amistatList.filter { it.nom.contains(searchText, ignoreCase = true) }) {item ->
                    AmistatListItem(name = item.nom, onClick = {onAmistatClick(item.id)})
                }
            } else if (currentMode == Selector.USUARIS) {
                items(usuarisList.filter {  it.nom?.contains(searchText, ignoreCase = true) ?: false}){
                    user -> UsuariListItem(name = user.nom, onSeguirClick = {viewModel.seguir_usuari(accepta = user?.correu)})
                }
            } else if (currentMode == Selector.ENVIADES){
                items(all_enviades){
                        user -> EnviadesListItem(name = user.nom, onCancelar = {viewModel.cancelar_solicitud_enviada(user.xat_id)})
                }
            } else {
                items(all_rebudes){
                        user -> RebudesListItem(name = user.nom, onCancelar = {viewModel.cancelar_solicitud_rebuda(user.xat_id)}, onAcceptar = {viewModel.aceptar_solicitud_rebuda(user.xat_id)})
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
fun UsuariListItem(
    name: String?,
    onSeguirClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = onSeguirClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Seguir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun RebudesListItem(
    name: String?,
    onCancelar: () -> Unit,
    onAcceptar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = onAcceptar) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seguir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onCancelar) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Seguir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EnviadesListItem(
    name: String?,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = onCancelar) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Seguir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
