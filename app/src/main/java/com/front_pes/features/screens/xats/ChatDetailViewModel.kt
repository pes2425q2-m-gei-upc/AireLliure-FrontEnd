@file:Suppress("detekt")
package com.front_pes.features.screens.xats

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.front_pes.network.RetrofitClient
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ChatDetailViewModel : ViewModel() {

    // Representació interna del missatge (adaptable si vols mostrar-ho diferent)
    data class Missatge(
        val id: Int,
        val text: String,
        val data: String,
        val xat: Int,
        val autor: String?,
        val nom: String? = null
    )


    var missatges by mutableStateOf<List<Missatge>>(emptyList())
    var errorMessage by mutableStateOf<String?>(null)

    fun carregarMissatges(chatId: Int) {
        val call = RetrofitClient.apiService.getChatDetail(chatId)
        call.enqueue(object : Callback<ChatDetailResponse> {
            override fun onResponse(call: Call<ChatDetailResponse>, response: Response<ChatDetailResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { resposta ->
                        Log.d("WebSocket", "Missatges: ${response.body()}")
                        missatges = resposta.missatges.map { msg ->
                            Missatge(
                                id = msg.id,
                                text = msg.text,
                                data = msg.data,
                                xat = msg.xat,
                                autor = msg.autor,
                                nom = msg.nom
                            )
                        }
                    }
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ChatDetailResponse>, t: Throwable) {
                errorMessage = "Error de xarxa: ${t.message}"
            }
        })
    }
    fun enviarMissatge(
        text: String,
        xat: Int,
        autor: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("Europe/Madrid")
        val dataActual = formatter.format(Date())

        val missatge = SendMessageRequest(
            text = text,
            data = dataActual,
            xat = xat,
            autor = autor
        )

        val call = RetrofitClient.apiService.enviarMissatge(missatge)
        call.enqueue(object : Callback<SendMessageResponse> {
            override fun onResponse(call: Call<SendMessageResponse>, response: Response<SendMessageResponse>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al enviar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SendMessageResponse>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }
    fun editarMissatge(
        missatgeOriginal: Missatge,
        textNou: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("Europe/Madrid")
        val dataOriginal = missatgeOriginal.data

        val request = UpdateMessageRequest(
            text = textNou,
            data = dataOriginal, // mantener la original
            xat = missatgeOriginal.xat,
            autor = missatgeOriginal.autor ?: ""
        )

        val call = RetrofitClient.apiService.updateMissatge(missatgeOriginal.id, request)
        call.enqueue(object : Callback<UpdateMessageResponse> {
            override fun onResponse(call: Call<UpdateMessageResponse>, response: Response<UpdateMessageResponse>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateMessageResponse>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }
    fun esborrarMissatge(
        missatgeId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitClient.apiService.deleteMissatge(missatgeId)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al esborrar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Error de xarxa: ${t.message}")
            }
        })
    }
    var isGroup by mutableStateOf(false)

    fun detectarSiEsGrup(chatId: Int) {
        val call = RetrofitClient.apiService.getXatGrupalById(chatId)
        call.enqueue(object : Callback<GroupDetailResponse> {
            override fun onResponse(call: Call<GroupDetailResponse>, response: Response<GroupDetailResponse>) {
                isGroup = response.isSuccessful
            }

            override fun onFailure(call: Call<GroupDetailResponse>, t: Throwable) {
                isGroup = false
            }
        })
    }

    private var webSocket: WebSocket? = null

    fun iniciarWebSocket(chatId: Int) {
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
                    if (modelo == "Missatge") {
                        carregarMissatges(chatId)
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
