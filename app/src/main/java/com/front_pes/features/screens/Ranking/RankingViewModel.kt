package com.front_pes.features.screens.Ranking

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.xats.LlistaXatResponse
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

class RankingViewModel: ViewModel() {

    data class ranking(val name:String, val points: Int)
    /* VARAIBLE DE REBUDA DE DADES PER ALL*/
    var ranking_all_users by mutableStateOf <List<ranking>>(emptyList())
    /* VARAIBLE DE REBUDA DE DADES PER QUAN SIGUI NOMÉS AMICS*/
    var ranking_amics by mutableStateOf <List<ranking>>(emptyList())
    /* VARAIBLE DE ERRORS */
    var errorMessage by mutableStateOf <String?>("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init{
        ranking_tt_users()
        ranking_n_amics()
    }

    fun ranking_tt_users()= viewModelScope.launch{
        _isLoading.value = true;
        try {
            val call = RetrofitClient.apiService.get_all_ranking()
            call.enqueue(object: Callback<List<RankingResponse>>{
               override fun onResponse(
                   call: Call<List<RankingResponse>>,
                   response: Response<List<RankingResponse>>
               ) {
                   if(response.code() == 200){
                       val resposta = response.body()
                       resposta?.let{
                           ranking_all_users = it.map { item -> ranking(name = item.nom, points = item.punts) }
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
               override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                   errorMessage = "Error al carregar el Ranking: ${t.message}"
                   _isLoading.value = false;
               }
            })
        } catch(e:Exception){
           println("Error al intentar carregar el ranking: ${e.message}")
            _isLoading.value = false;
        }
    }

    fun ranking_n_amics()= viewModelScope.launch{
        try{
            _isLoading.value = true;
            val call = RetrofitClient.apiService.get_ranking_amistats(CurrentUser.correu)
            call.enqueue(object: Callback<List<RankingResponse>>{

                override fun onResponse(
                    call: Call<List<RankingResponse>>,
                    response: Response<List<RankingResponse>>
                ) {

                    if(response.code() == 200){
                        val resposta = response.body()
                        resposta?.let {
                            ranking_amics = it.map { item -> ranking(name = item.nom, points = item.punts) }
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
                override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                    println("Error al carregar el ranking d'amics: ${t.message}")
                    _isLoading.value = false;
                }
            })
        } catch(e:Exception){
            println("Error en carregar el ranking d'amics: ${e.message}")
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
                    if (modelo == "Usuario") {
                        ranking_tt_users();
                        ranking_n_amics();
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