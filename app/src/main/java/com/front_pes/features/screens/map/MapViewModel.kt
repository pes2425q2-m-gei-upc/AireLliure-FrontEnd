package com.front_pes.features.screens.map

import androidx.lifecycle.ViewModel
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MapViewModel : ViewModel() {

    private val _rutes = MutableStateFlow<List<RutasResponse>>(emptyList())
    val rutes: StateFlow<List<RutasResponse>> = _rutes

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
                            onSuccess(rutas)
                        } ?: run {
                            onError("Respuesta vacía de la API")
                        }
                    } else {
                        onError("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<RutasResponse>>, t: Throwable) {
                    onError("Error de red: ${t.message}")
                }
            })
        }
    }

    fun fetchPuntByID(
        pk: Int,
        onSuccess: (PuntsResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val call = RetrofitClient.apiService.getPuntByID(pk)
            call.enqueue(object : Callback<PuntsResponse> {
                override fun onResponse(
                    call: Call<PuntsResponse>,
                    response: Response<PuntsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { punt ->
                            onSuccess(punt)
                        } ?: run {
                            onError("Respuesta vacía")
                        }
                    } else {
                        onError("Error respuesta: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<PuntsResponse>, t: Throwable) {
                    onError("Fallo de red: ${t.message}")
                }
            })
        }
    }
    var alreadyAskedPermission = false
    var hasShownPermissionWarning = false
}
