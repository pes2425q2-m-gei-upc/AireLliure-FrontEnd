// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.front_pes.CurrentUser
import com.front_pes.features.screens.ActivitatsEvents.ActivityPrivRequest
import com.front_pes.features.screens.ActivitatsEvents.ActivityResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupDetailViewModel : ViewModel() {

    var nom by mutableStateOf("")
    var descripcio by mutableStateOf("")
    var creador by mutableStateOf("")
    var membres by mutableStateOf<List<String>>(emptyList())
    var amistats by mutableStateOf<List<LlistaAmistatResponse>>(emptyList())
    var errorMessage by mutableStateOf<String?>(null)
    var activitats by mutableStateOf<List<ActivityResponse>>(emptyList())

    @OptIn(UnstableApi::class)
    suspend fun carregar_activitats(id:Int){
        val resposta = RetrofitClient.apiService.get_activitats_by_xat(id)
        activitats = resposta
    }

    fun crearActivitatPrivada(
        nom: String,
        desc: String,
        data_inici: String,
        data_fi: String,
        groupId: Int
    )=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.create_new_event_privat(ActivityPrivRequest(nom=nom, descripcio = desc, data_inici= data_inici, data_fi = data_fi, creador = CurrentUser.correu, xat=groupId))
            Log.d("resp",resposta.code().toString())
            if (resposta.isSuccessful){
                Log.d("resp","Perfecte")
            } else {
                Log.d("resp","Error al crear event privat: código ${resposta.code()}, errorBody: ${resposta.errorBody()?.string()}")
            }
            carregar_activitats(groupId)
        }catch(e:Exception){
            Log.d("resp","Error al crear event public: ${e.message}")
        }
    }

    fun carregarGrup(id: Int) {
        RetrofitClient.apiService.getXatGrupalById(id)
            .enqueue(object : Callback<GroupDetailResponse> {
                override fun onResponse(
                    call: Call<GroupDetailResponse>,
                    response: Response<GroupDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { grup ->
                            nom = grup.nom
                            descripcio = grup.descripcio ?: ""
                            creador = grup.creador
                            membres = grup.membres.toMutableList()
                        }
                    }
                }

                override fun onFailure(call: Call<GroupDetailResponse>, t: Throwable) {
                    println("Error carregant grup: ${t.message}")
                }
            })
    }

    fun carregarAmistats() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
                amistats = result
            } catch (e: Exception) {
                errorMessage = "Error de xarxa: ${e.message}"
            }
        }
    }

    fun actualitzarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val body = GroupUpdateRequest(
            nom = nom,
            creador = creador,
            descripcio = descripcio,
            membres = membres
        )
        RetrofitClient.apiService.updateXatGrupal(id, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }

    fun toggleMembre(correu: String) {
        membres = if (membres.contains(correu)) {
            membres - correu
        } else {
            membres + correu
        }
    }

    fun esborrarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        RetrofitClient.apiService.deleteXatGrupal(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }

    fun abandonarGrup(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        membres = membres.filterNot { it == CurrentUser.correu } // elimina el correu propi
        val body = GroupUpdateRequest(
            nom = nom,
            creador = creador,
            descripcio = descripcio,
            membres = membres
        )
        RetrofitClient.apiService.updateXatGrupal(id, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) onSuccess()
                else onError("Error: ${response.code()}")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }

    private var webSocket: WebSocket? = null

    fun iniciarWebSocket(id: Int) {
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
                    if (modelo == "XatGrupal") {
                        carregarGrup(id);
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
