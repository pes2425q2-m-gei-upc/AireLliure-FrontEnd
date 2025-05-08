package com.front_pes.features.screens.xamistat

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
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString


@Composable
fun BloqueigScreen(onNavigateToRelations: () -> Unit, viewModel: BloqueigViewModel = viewModel()) {

    val scrollState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    var selected_nav by remember { mutableStateOf(BottomNavItem.Relacions) }
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    val labelRelacions = getString(context, R.string.Relacions, selectedLanguage)
    val labelBloqueigs = getString(context, R.string.bloqueo, selectedLanguage)

    LaunchedEffect(Unit) { viewModel.get_all_bloquejats()}
    val tots_bloqueig = viewModel.usuaris_bloquejats
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
                text = (getString(context, R.string.bloqueo, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color =  MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text(text = (getString(context, R.string.buscusu, selectedLanguage))) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            items(tots_bloqueig){
                item -> ListItem(name = item.id_correu_usuari, onCancelar = {viewModel.delete_bloqueig(item.id)})
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                label = { Text(labelRelacions) },
                selected = selected_nav == BottomNavItem.Relacions,
                onClick = { onNavigateToRelations() }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                label = { Text(labelBloqueigs) },
                selected = selected_nav == BottomNavItem.Bloqueigs,
                onClick = { }
            )
        }
    }
}

@Composable
fun ListItem(
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
                        contentDescription = "Eliminar Bloqueig",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}