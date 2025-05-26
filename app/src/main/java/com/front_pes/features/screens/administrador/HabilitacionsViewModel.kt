@file:Suppress("detekt")
package com.front_pes.features.screens.administrador

import android.util.Log
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HabilitacionsViewModel : ViewModel() {
    data class detallUser(val correu: String, val nom: String)
    /*
    Variable deshabilitats
     */
    var deshabilitats by mutableStateOf<List<detallUser>>(emptyList())
    /*
    Variable de tots els habilitats
  */
    var habilitats by mutableStateOf<List<detallUser>>(emptyList())
    /*
    VARIABLE DE TOTS ELS USUARIS HABILITATS
     */
    var errorMessage by mutableStateOf<String?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun get_all_usuaris_habilitats()=viewModelScope.launch {
        _isLoading.value = true;
        try {
            val resposta = RetrofitClient.apiService.gethabilitats()
            println("1    " + resposta)
            habilitats = resposta.map { item ->  detallUser(correu = item.correu, nom = item.nom)}
            _isLoading.value = false;
        } catch(e:Exception){
            println("Error al carregar tots els usuaris habilitats de l'APP: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun get_all_usuaris_deshabilitats()= viewModelScope.launch {
        _isLoading.value = true;
        try {
            val respost = RetrofitClient.apiService.getdeshabilitats()
            println("2   " + respost)
            deshabilitats = respost.map { item -> detallUser(correu = item.correu, nom = item.nom)}
            _isLoading.value = false;
        } catch(e:Exception){
            println("Error al carregar els usuaris deshabilitats de l'APP: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun deshabilitar(correu: String)=viewModelScope.launch {
        _isLoading.value = true;
        try{
            println("entro deshabilitar" + "correu: " + correu)
            val resp = RetrofitClient.apiService.deshabilitar(CurrentUser.correu, correu)
            get_all_usuaris_habilitats()
            get_all_usuaris_deshabilitats()
        } catch(e:Exception){
            println("Error al executar la funcio de deshabilitar: ${e.message}")
        }
    }
    fun rehabilitar(correu: String)= viewModelScope.launch {
        _isLoading.value = true;
        try{
            println("entro rehabilitar" + "correu: " + correu)

            val respo = RetrofitClient.apiService.rehabilitar(correu)
            get_all_usuaris_habilitats()
            get_all_usuaris_deshabilitats()
        } catch(e:Exception){
            println("Error al rehabilitar l'usuari : ${e.message}")
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
                    if (modelo == "Usuario") {
                        get_all_usuaris_habilitats();
                        get_all_usuaris_deshabilitats();
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
}