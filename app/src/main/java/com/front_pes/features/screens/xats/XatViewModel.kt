// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class XatViewModel: ViewModel() {
    /* VARIABLE ON ES GUARDARAN LES DADES DE RETORN DE LA PETICIO*/
    data class Xat(val id: Int, val nom: String, val imatge:String?=null)
    var xats by mutableStateOf <List<Xat>>(emptyList())
    /* VARIABLE EN CAS D'ERROR*/
    var errorMessage by mutableStateOf<String?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        carregarXats()
    }

    fun carregarXats() = viewModelScope.launch {
        _isLoading.value = true;
        try {
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
                                item -> Xat(id = item.id, nom= item.nom, imatge = item.imatge)
                            }
                        }
                        _isLoading.value = false;
                    } else {
                        errorMessage = when (response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                        _isLoading.value = false;
                    }
                }
                override fun onFailure(call: Call<List<LlistaXatResponse>>, t: Throwable) {
                    errorMessage = "Network error: ${t.message}"
                    _isLoading.value = false;
                }
            })
        } catch (e: Exception) {
            println("Error carregant xats: ${e.message}")
            _isLoading.value = false;
        }
    }

    private var webSocket: WebSocket? = null

    fun iniciarWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("wss://airelliure-backend.onrender.com/ws/modelos/").build() // Asegúrate de usar tu URL real
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Conexión abierta")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Mensaje recibido: $text")

                try {
                    val json = JSONObject(text)
                    val modelo = json.optString("modelo")
                    if (modelo == "XatIndividual" ||modelo == "XatGrupal") {
                        carregarXats()
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error procesando mensaje: ${e.message}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                Log.d("WebSocket", "Conexión cerrándose: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, null)
    }
}