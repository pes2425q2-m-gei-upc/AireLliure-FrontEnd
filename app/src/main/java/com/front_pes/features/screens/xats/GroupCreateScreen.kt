// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

//test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString

@Composable
fun GroupCreateScreen(
    onGroupCreated: (chatId: Int, groupName: String) -> Unit,
    onBack: () -> Unit,
    viewModel: GroupCreateViewModel = viewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarAmistats()
        viewModel.iniciarWebSocket()
    }

    LaunchedEffect(viewModel.amistats) {
        viewModel.carregarAmistats()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {

            Text(text = (getString(context, R.string.creagrup, selectedLanguage)), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text(text = (getString(context, R.string.nomgrup, selectedLanguage))) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = groupDesc,
                onValueChange = { groupDesc = it },
                label = { Text(text = (getString(context, R.string.desc, selectedLanguage))) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = (getString(context, R.string.selectmem, selectedLanguage)), style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.amistats) { item ->
                    val checked = item.correu in viewModel.membresSeleccionats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (checked) viewModel.membresSeleccionats.remove(item.correu)
                                else viewModel.membresSeleccionats.add(item.correu)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.nom)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onBack) {
                    Text(text = (getString(context, R.string.volver, selectedLanguage)))
                }

                Button(
                    onClick = {
                        viewModel.crearGrup(
                            nom = groupName,
                            descripcio = groupDesc,
                            onSuccess = { chatId ->
                                onGroupCreated(chatId, groupName)
                            },
                            onError = { println("Error: $it") }
                        )
                    },
                    enabled = groupName.isNotBlank() && viewModel.membresSeleccionats.isNotEmpty()
                ) {
                    Text(text = (getString(context, R.string.creagrup, selectedLanguage)))
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
