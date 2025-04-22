package com.front_pes.features.screens.xamistat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetallAmistatViewModel: ViewModel() {

    /* VARIABLE ON ES GUARDA LA CRIDA DE LA API  */
    var usuari by mutableStateOf<DetallUsuari?>(null)
    /* VARIABLE DE ERROR */
    var errorMessage by mutableStateOf<String?>(null)

    fun getDetallAmic(userID: String) = viewModelScope.launch{
        try{
            val call = RetrofitClient.apiService.getDetallUsuariAmic(userID)
            call.enqueue(object: Callback <DetallUsuariResponse>{

                override fun onResponse(
                    call: Call<DetallUsuariResponse>,
                    response: Response<DetallUsuariResponse>
                ) {
                    if (response.code() == 200) {
                        val resposta = response.body()
                        resposta?.let {
                            usuari = DetallUsuari(
                                correu = it.correu,
                                nom = it.nom,
                                about = it.about,
                                punts = it.punts
                            )
                        }
                    } else {
                        errorMessage = when (response.code()){
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<DetallUsuariResponse>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                }
            })
        } catch(e: Exception){
            println("Error carregant les dades especifiques de un usuari: ${e.message}")
        }
    }
    fun bloquejar_usuari()=viewModelScope.launch {
        try{
            val respo = RetrofitClient.apiService.crear_bloqueig(BloqueigRequest(bloqueja=CurrentUser.correu, bloquejat = usuari?.correu ?:""))

        } catch(e:Exception){
            println("Error al intentar bloquejar aquest compte: ${e.message}")
        }
    }
}