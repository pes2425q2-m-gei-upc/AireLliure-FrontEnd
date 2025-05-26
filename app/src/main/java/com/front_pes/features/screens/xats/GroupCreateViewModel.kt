// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
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

class GroupCreateViewModel : ViewModel() {

    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
    var membresSeleccionats = mutableStateListOf<String>()
    var errorMessage by mutableStateOf<String?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun carregarAmistats() {
        _isLoading.value = true;
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
                amistats = result
                _isLoading.value = false;
            } catch (e: Exception) {
                errorMessage = "Error de xarxa: ${e.message}"
                _isLoading.value = false;
            }
        }
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
            descripcio = descripcio,
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
                        carregarAmistats();
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
