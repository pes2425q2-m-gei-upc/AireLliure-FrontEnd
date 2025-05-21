package com.front_pes.features.screens.xamistat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetallAmistatViewModel: ViewModel() {

    /* VARIABLE ON ES GUARDA LA CRIDA DE LA API  */
    var usuari by mutableStateOf<DetallUsuari?>(null)
    /* VARIABLE DE ERROR */
    var errorMessage by mutableStateOf<String?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getDetallAmic(userID: String) = viewModelScope.launch{
        _isLoading.value = true;
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
                                punts = it.punts,
                                imatge = it.imatge
                            )
                        }
                        _isLoading.value = false;
                    } else {
                        errorMessage = when (response.code()){
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                        _isLoading.value = false;
                    }
                }
                override fun onFailure(call: Call<DetallUsuariResponse>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                    _isLoading.value = false;
                }
            })
        } catch(e: Exception){
            println("Error carregant les dades especifiques de un usuari: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun bloquejar_usuari()=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val respo = RetrofitClient.apiService.crear_bloqueig(BloqueigRequest(bloqueja=CurrentUser.correu, bloquejat = usuari?.correu ?:""))
            _isLoading.value = false;
        } catch(e:Exception){
            println("Error al intentar bloquejar aquest compte: ${e.message}")
            _isLoading.value = false;
        }
    }
}