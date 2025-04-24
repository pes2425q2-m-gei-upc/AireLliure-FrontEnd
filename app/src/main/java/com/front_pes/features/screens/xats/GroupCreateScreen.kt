package com.front_pes.features.screens.xats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupCreateScreen(
    onGroupCreated: (chatId: Int, groupName: String) -> Unit,
    onBack: () -> Unit,
    viewModel: GroupCreateViewModel = viewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.carregarAmistats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crear Grup", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Nom del grup") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = groupDesc,
            onValueChange = { groupDesc = it },
            label = { Text("Descripció") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Selecciona membres:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.amistats) { item ->
                val checked = item.correu in viewModel.membresSeleccionats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (checked) {
                                viewModel.membresSeleccionats.remove(item.correu)
                            } else {
                                viewModel.membresSeleccionats.add(item.correu)
                            }
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
                Text("Tornar")
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
                Text("Crear grup")
            }
        }
    }
}
