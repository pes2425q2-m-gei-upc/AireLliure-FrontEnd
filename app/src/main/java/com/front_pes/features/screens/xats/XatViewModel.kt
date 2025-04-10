package com.front_pes.features.screens.xats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.xats.LlistaXatRequest
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.xats.LlistaXatResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class XatViewModel: ViewModel() {
    /* VARIABLE ON ES GUARDARAN LES DADES DE RETORN DE LA PETICIO*/
    data class Xat(val id: Int, val nom: String)
    var xats by mutableStateOf <List<Xat>>(emptyList())
    /* VARIABLE EN CAS D'ERROR*/
    var errorMessage by mutableStateOf<String?>(null)

    init {
        carregarXats()
    }


    fun carregarXats() = viewModelScope.launch {
        try {
            Log.d("USERRRRRRR","EL VALOR : $CurrentUser.correu")
            val call = RetrofitClient.apiService.getXatsUsuaribyCorreu(CurrentUser.correu) // aqui poder nomes cal pasar l'idnetificador.
            call.enqueue(object : Callback<List<LlistaXatResponse>>{
                override fun onResponse(
                    call: Call<List<LlistaXatResponse>>,
                    response: Response<List<LlistaXatResponse>>
                ) {
                    if (response.code()== 200){
                        val resposta = response.body()
                        resposta?.let {
                            xats = it.map{
                                item -> Xat(id = item.id, nom= item.nom)
                            }
                        }
                    } else {
                        errorMessage = when (response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<List<LlistaXatResponse>>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                }
            })
        } catch (e: Exception) {
            println("Error carregant xats: ${e.message}")
        }
    }
}