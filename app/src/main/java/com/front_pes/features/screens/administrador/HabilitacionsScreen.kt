package com.front_pes.features.screens.administrador
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
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

const val HabilitacionsScreen = "HabilitacioListScreen"
enum class Selector{
    ALL,
    DESHABILITATS
}

@Composable
fun HabilitacionsScreen(viewModel: HabilitacionsViewModel = viewModel()) {

    var currentMode by remember {mutableStateOf(Selector.ALL)}
    val scrollState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.get_all_usuaris_habilitats()}
    LaunchedEffect(Unit) { viewModel.get_all_usuaris_deshabilitats()}
    val usuarishabilitats = viewModel.habilitats
    val usuarisdeshabilitats = viewModel.deshabilitats

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
                text = (getString(context, R.string.USU, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.ALL) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.ALL }
                    .padding(10.dp)
            )

            Text(
                text = (getString(context, R.string.DESHAB, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.DESHABILITATS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable { currentMode = Selector.DESHABILITATS }
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
            if(currentMode == Selector.ALL){
                items(usuarishabilitats.filter { it.nom.contains(searchText, ignoreCase = true) }) {item ->
                    AllListItem(name = item.nom, onDeshabilitar ={viewModel.deshabilitar(item.correu)})
                }
            } else if (currentMode == Selector.DESHABILITATS) {
                items(usuarisdeshabilitats.filter {  it.nom.contains(searchText, ignoreCase = true) ?: false}){
                        user -> HabilitacioListItem(name = user.nom, onReHabilitar = {viewModel.rehabilitar(user.correu)})
                }
            }
        }
    }
}

@Composable
fun AllListItem(name: String, onDeshabilitar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {},
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
            Row(  verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onDeshabilitar,
                    /*modifier = Modifier.padding(start = 180.dp)*/) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar Amistad",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun HabilitacioListItem(
    name: String?,
    onReHabilitar: () -> Unit
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
                IconButton(onClick = onReHabilitar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Seguir",
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}