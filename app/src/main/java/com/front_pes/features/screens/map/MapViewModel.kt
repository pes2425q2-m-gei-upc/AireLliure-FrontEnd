package com.front_pes.features.screens.map

import androidx.lifecycle.ViewModel
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope

class MapViewModel : ViewModel() {
    fun fetchEstacionsQualitatAire(onSuccess: (List<EstacioQualitatAireResponse>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val call = RetrofitClient.apiService.getEstacionsQualitatAire()
            call.enqueue(object : Callback<List<EstacioQualitatAireResponse>> {
                override fun onResponse(
                    call: Call<List<EstacioQualitatAireResponse>>,
                    response: Response<List<EstacioQualitatAireResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { estacions ->
                            onSuccess(estacions)
                        } ?: run {
                            onError("Respuesta vacía de la API")
                        }
                    } else {
                        onError("Error desconocido: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<EstacioQualitatAireResponse>>, t: Throwable) {
                    onError("Network error: ${t.message}")
                }
            })
        }
    }

    fun fetchRutes(onSuccess: (List<RutasResponse>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val call = RetrofitClient.apiService.getRutas()
            call.enqueue(object : Callback<List<RutasResponse>> {
                override fun onResponse(
                    call: Call<List<RutasResponse>>,
                    response: Response<List<RutasResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { rutas ->
                            android.util.Log.d("API_RUTES", "Datos recibidos: $rutas")
                            onSuccess(rutas)
                        } ?: run {
                            onError("Respuesta vacía de la API")
                            android.util.Log.e("API_RUTES", "Respuesta vacía")
                        }
                    } else {
                        onError("Error: ${response.code()}")
                        android.util.Log.e("API_RUTES", "Código error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<RutasResponse>>, t: Throwable) {
                    onError("Error de red: ${t.message}")
                    android.util.Log.e("API_RUTES", "Fallo en la petición: ${t.message}")
                }
            })
        }
    }


}
