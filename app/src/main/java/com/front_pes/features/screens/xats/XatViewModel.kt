package com.front_pes.features.screens.register

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

data class Xat(val id: Int, val nom: String)

class XatViewModel : ViewModel() {

    // variable global de rebre la peticio de la api i mostrar-ho al front-end.

    init {
        carregarXats()
    }

    var errorMessage by mutableStateOf<String?>(null)
    // necessitariem una variable que guardes dades i que les passes per printar-les al screen.
    private fun carregarXats() {
        viewModelScope.launch {
            try {
               val call = RetrofitClient.apiService.getXatsUsuaribyCorreu(CurrentUser.correu, LlistaXatRequest(correu = CurrentUser.correu)) // aqui poder nomes cal pasar l'idnetificador.
               call.enqueue(object : Callback<LlistaXatResponse>{
                    override fun onResponse(
                        call: Call<LlistaXatResponse>,
                        response: Response<LlistaXatResponse>
                    ) {
                        if (response.code()== 200){
                            // aqui necessito una variable que m'aguanti també en Front, en aquest cas en el Screen.

                        } else {
                            errorMessage = when (response.code()) {
                                404 -> "usuari no existeix"
                                401 -> "contrasenya incorrecta"
                                else -> "Error desconocido: ${response.code()}"
                            }
                        }
                    }

                   override fun onFailure(call: Call<LlistaXatResponse>, t: Throwable) {
                       errorMessage = "Network error: ${t.message}"
                   }
                })
            } catch (e: Exception) {
                println("Error carregant xats: ${e.message}")
            }
        }
    }
}