package com.front_pes.features.screens.xamistat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LlistatAmistatViewModel: ViewModel() {

    data class AmistatLine(val id: String, val nom: String, val correu: String)
    data class UsuariLine(val id: String?=null, val nom: String?=null, val correu: String?=null)
    /* VAR DEL LLISTAT DE AMICS */
    var llista_amics by mutableStateOf <List<AmistatLine>>(emptyList())
    /* VAR DE TOTS ELS USUARIS PER AL SELECTOR   */
    var all_users by mutableStateOf <List<UsuariLine>>(emptyList())
    /*  VAR PER ALS ERRORS */
    var errorMessage by mutableStateOf <String?>(null)

    init {
        getXatsAmics()
    }

    fun getXatsAmics() = viewModelScope.launch {
        try{
            val call = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
            call.enqueue(object: Callback<List<LlistaAmistatResponse>>{
                override fun onResponse(
                    call: Call<List<LlistaAmistatResponse>>,
                    response: Response<List<LlistaAmistatResponse>>
                ) {
                    if (response.code()==200){

                        val resposta = response.body()
                        resposta?.let {
                            llista_amics = it.map { item -> AmistatLine(id=item.correu, nom= item.nom, correu = item.correu) }
                        }
                    } else {
                        errorMessage = when(response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<List<LlistaAmistatResponse>>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                }
            })
        } catch (e: Exception){
            println("Error carregant el llistat d'amics: ${e.message}")
        }
    }

    fun get_usuaris() = viewModelScope.launch{
        try{
            val call = RetrofitClient.apiService.get_all_usuaris()
            call.enqueue(object: Callback<List<DetallUsuariResponse>>{

                override fun onResponse(
                    call: Call<List<DetallUsuariResponse>>,
                    response: Response<List<DetallUsuariResponse>>
                ) {
                    if(response.code() == 200){

                        val resposta_ = response.body()
                        resposta_?.let {
                            all_users = it.map { item -> UsuariLine(id = item.correu, nom = item.nom, correu = item.correu) }
                        }
                    } else {
                        errorMessage = when(response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<List<DetallUsuariResponse>>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                }
            })
        } catch(e:Exception){
            println("Error al carregar els usuaris : ${e.message}")
        }
    }

    fun seguir_usuari(accepta:String?)=viewModelScope.launch {
        try{
            if (accepta == null) {
                println("No pots seguir un usuari amb ID null")
                return@launch
            }
            val call = RetrofitClient.apiService.create_new_amistat(SolicitarAmistatRequest(solicita = CurrentUser.correu, accepta = accepta, pendent = true))
            call.enqueue(object: Callback<SolicitarAmistatResponse>{
                override fun onResponse(
                    call: Call<SolicitarAmistatResponse>,
                    response: Response<SolicitarAmistatResponse>
                ) {
                    if(response.code() == 200){
                        println("Solicitud feta")
                    } else {
                        errorMessage = when(response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<SolicitarAmistatResponse>, t: Throwable) {
                    println("Error al fer la peticio de solicitud: ${t.message}")
                }
            })
        } catch(e: Exception){
            println("Error al fer la peticio de solicitud: ${e.message}")
        }
    }
}

