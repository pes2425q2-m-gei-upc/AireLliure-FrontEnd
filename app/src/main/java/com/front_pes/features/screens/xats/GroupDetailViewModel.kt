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

class GroupDetailViewModel : ViewModel() {

    var nom by mutableStateOf("")
    var descripcio by mutableStateOf("")
    var creador by mutableStateOf("")
    var membres by mutableStateOf<List<String>>(emptyList())
    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
    var errorMessage by mutableStateOf<String?>(null)

    fun carregarGrup(id: Int) {
        RetrofitClient.apiService.getXatGrupalById(id)
            .enqueue(object : Callback<GroupDetailResponse> {
                override fun onResponse(
                    call: Call<GroupDetailResponse>,
                    response: Response<GroupDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { grup ->
                            nom = grup.nom
                            descripcio = grup.descripcio ?: ""
                            creador = grup.creador
                            membres = grup.membres.toMutableList()
                        }
                    }
                }

                override fun onFailure(call: Call<GroupDetailResponse>, t: Throwable) {
                    println("Error carregant grup: ${t.message}")
                }
            })
    }



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

    fun actualitzarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val body = GroupUpdateRequest(
            nom = nom,
            creador = creador,
            descripcio = descripcio,
            membres = membres
        )
        RetrofitClient.apiService.updateXatGrupal(id, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }

    fun toggleMembre(correu: String) {
        membres = if (membres.contains(correu)) {
            membres - correu
        } else {
            membres + correu
        }
    }

    fun esborrarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        RetrofitClient.apiService.deleteXatGrupal(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }

    fun abandonarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        membres = membres.filterNot { it == CurrentUser.correu } // elimina el correu propi
        val body = GroupUpdateRequest(
            nom = nom,
            creador = creador,
            descripcio = descripcio,
            membres = membres
        )
        RetrofitClient.apiService.updateXatGrupal(id, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }


}
