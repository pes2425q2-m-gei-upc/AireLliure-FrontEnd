package com.front_pes.features.screens.xats

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupCreateViewModel : ViewModel() {

    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
    var membresSeleccionats = mutableStateListOf<String>()
    var errorMessage by mutableStateOf<String?>(null)

    fun carregarAmistats() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
                amistats = result
            } catch (e: Exception) {
                errorMessage = "Error de xarxa: ${e.message}"
            }
        }
    }

    fun crearGrup(
        nom: String,
        descripcio: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val membres = membresSeleccionats.toMutableList().apply {
            add(CurrentUser.correu)
        }

        val request = GroupCreateRequest(
            nom = nom,
            creador = CurrentUser.correu,
            descripcio = descripcio,
            membres = membres.distinct()
        )

        RetrofitClient.apiService.createXatGrupal(request).enqueue(object : Callback<GroupCreateResponse> {
            override fun onResponse(call: Call<GroupCreateResponse>, response: Response<GroupCreateResponse>) {
                if (response.isSuccessful) {
                    response.body()?.id?.let { onSuccess(it) }
                } else {
                    onError("Error creant grup: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GroupCreateResponse>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }
}
