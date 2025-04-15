package com.front_pes.features.screens.xats

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.front_pes.CurrentUser
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupCreateViewModel : ViewModel() {

    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
    var membresSeleccionats = mutableStateListOf<String>()
    var errorMessage by mutableStateOf<String?>(null)

    fun carregarAmistats() {
        val call = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
        call.enqueue(object : Callback<List<LlistaAmistatResponse>> {
            override fun onResponse(call: Call<List<LlistaAmistatResponse>>, response: Response<List<LlistaAmistatResponse>>) {
                if (response.isSuccessful) {
                    amistats = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error carregant amistats: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<LlistaAmistatResponse>>, t: Throwable) {
                errorMessage = "Error de xarxa: ${t.message}"
            }
        })
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
            descripció = descripcio,
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
