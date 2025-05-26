package com.front_pes.features.screens.xamistat


import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString


const val LlistatAmistatScreen = "AmistatListScreen"

@Composable
fun FotoUsuari(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "user picture",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_user), // opcional
        error = painterResource(R.drawable.ic_user)         // opcional
    )
}

enum class Selector {
    AMISTATS,
    USUARIS,
    REBUDES,
    ENVIADES
}

enum class BottomNavItem() {
    Relacions,
    Bloqueigs
}


@Composable
fun LlistatAmistatScreen(
    onAmistatClick: (String) -> Unit,
    onNavigateToBlocks: () -> Unit,
    viewModel: LlistatAmistatViewModel = viewModel()
) {

    var currentMode by remember { mutableStateOf(Selector.AMISTATS) }
    val scrollState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    var selected_nav by remember { mutableStateOf(BottomNavItem.Relacions) }
    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getXatsAmics();
        viewModel.iniciarWebSocket();
    }
    LaunchedEffect(Unit) { viewModel.get_usuaris() }

    val amistatList = viewModel.llista_amics
    val usuarisList = viewModel.all_users
    val all_rebudes = viewModel.all_rebudes
    val all_enviades = viewModel.all_enviades
    val labelRelacions = getString(context, R.string.Relacions, selectedLanguage)
    val labelBloqueigs = getString(context, R.string.bloqueo, selectedLanguage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp, start = 10.dp, end = 24.dp),
    ) {
        val selectorScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(selectorScroll),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = (getString(context, R.string.amigos, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.AMISTATS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { currentMode = Selector.AMISTATS }
                    .padding(10.dp)
            )

            Text(
                text = (getString(context, R.string.usuarios, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.USUARIS) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { currentMode = Selector.USUARIS }
                    .padding(10.dp)
            )
            Text(
                text = (getString(context, R.string.pend, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.REBUDES) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { currentMode = Selector.REBUDES }
                    .padding(10.dp)
            )
            Text(
                text = (getString(context, R.string.enviado, selectedLanguage)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentMode == Selector.ENVIADES) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .clickable { currentMode = Selector.ENVIADES }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            if (currentMode == Selector.AMISTATS) {
                items(amistatList.filter {
                    it.nom.contains(
                        searchText,
                        ignoreCase = true
                    )
                }) { item ->
                    AmistatListItem(
                        name = item.nom,
                        imatge = item.imatge,
                        onClick = { onAmistatClick(item.id) },
                        onDelete = { viewModel.delete_amistad(item.idAmistat) })
                }
            } else if (currentMode == Selector.USUARIS) {
                items(usuarisList.filter {
                    it.nom?.contains(searchText, ignoreCase = true) ?: false
                }) { user ->
                    UsuariListItem(
                        name = user.nom,
                        imatge = user.imatge,
                        onSeguirClick = { viewModel.seguir_usuari(accepta = user.correu) })
                }
            } else if (currentMode == Selector.ENVIADES) {
                items(all_enviades) { user ->
                    EnviadesListItem(
                        name = user.nom,
                        imatge = user.imatge,
                        onCancelar = { viewModel.cancelar_solicitud_enviada(user.idAmistat) })
                }
            } else {
                items(all_rebudes) { user ->
                    RebudesListItem(
                        name = user.nom,
                        imatge = user.imatge,
                        onCancelar = { viewModel.cancelar_solicitud_rebuda(user.idAmistat) },
                        onAcceptar = { viewModel.aceptar_solicitud_rebuda(user.idAmistat) })
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                label = { Text(labelRelacions) },
                selected = selected_nav == BottomNavItem.Relacions,
                onClick = { selected_nav = BottomNavItem.Relacions }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                label = { Text(labelBloqueigs) },
                selected = selected_nav == BottomNavItem.Bloqueigs,
                onClick = { onNavigateToBlocks() }
            )
        }
    }
}

@Composable
fun UsuariListItem(
    name: String?,
    imatge: String?,
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

            if (imatge != null) {
                FotoUsuari(url = imatge)
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
            }
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
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}

@Composable
fun AmistatListItem(name: String, imatge: String?, onClick: () -> Unit, onDelete: () -> Unit) {
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imatge != null) {
                FotoUsuari(url = imatge)
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f)) // 👈 Esto empuja el botón a la derecha

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar Amistad",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun RebudesListItem(
    name: String?,
    imatge:String?,
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
            if (imatge != null) {
                FotoUsuari(url = imatge)
            } else{
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
            }
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
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun EnviadesListItem(
    name: String?,
    imatge:String?,
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
            if (imatge != null) {
                FotoUsuari(url = imatge)
            } else{
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
            }
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
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
