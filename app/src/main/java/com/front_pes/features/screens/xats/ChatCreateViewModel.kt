package com.front_pes.features.screens.xats

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.front_pes.CurrentUser
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse // ✅ IMPORT CORREGIDO
import com.front_pes.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatCreateViewModel : ViewModel() {

    data class XatSimple(val nom: String, val id: Int)

    var xatsExistents by mutableStateOf<List<XatSimple>>(emptyList())
    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
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

    fun crearXatIndividual(
        nom: String,
        usuari2: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val body = ChatCreateRequest(
            nom = nom,
            usuari1 = CurrentUser.correu,
            usuari2 = usuari2
        )

        val call = RetrofitClient.apiService.createXatIndividual(body)
        call.enqueue(object : Callback<ChatCreateResponse> {
            override fun onResponse(call: Call<ChatCreateResponse>, response: Response<ChatCreateResponse>) {
                if (response.isSuccessful) {
                    val chatId = response.body()?.id ?: return
                    onSuccess(chatId)
                } else {
                    onError("Error al crear: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ChatCreateResponse>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }
    fun carregarXats() {
        val call = RetrofitClient.apiService.getXatsUsuaribyCorreu(CurrentUser.correu)
        call.enqueue(object : Callback<List<LlistaXatResponse>> {
            override fun onResponse(call: Call<List<LlistaXatResponse>>, response: Response<List<LlistaXatResponse>>) {
                if (response.isSuccessful) {
                    xatsExistents = response.body()?.map { XatSimple(it.nom, it.id) } ?: emptyList()
                } else {
                    println("Error carregant xats: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LlistaXatResponse>>, t: Throwable) {
                println("Error de xarxa carregant xats: ${t.message}")
            }
        })
    }

}
