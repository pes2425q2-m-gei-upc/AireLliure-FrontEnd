package com.front_pes.features.screens.map

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import com.front_pes.SelectedContaminants
import com.front_pes.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MapViewModel : ViewModel() {

    private val _averageMap = mutableStateMapOf<Int, Double>()
    val averageMap: Map<Int, Double> get() = _averageMap

    private val _valuesMap = mutableStateMapOf<Int, Map<Int, Double>>()
    val valuesMap: Map<Int, Map<Int, Double>> get() = _valuesMap

    fun fetchAveragesForStations(
        stations: List<EstacioQualitatAireResponse>,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val tempMap = mutableMapOf<Int, Double>()
            val tempValuesMap = mutableMapOf<Int, Map<Int, Double>>()

            _averageMap.clear()
            _valuesMap.clear()

            stations.forEach { station ->
                val filters: Map<String, String> =
                    SelectedContaminants.selected.associateWith { it }

                try {
                    val response: Response<List<PresenciaResponse>> = withContext(Dispatchers.IO) {
                        apiService.getPresencia(station.id, filters).execute()
                    }

                    val validResponses = if (response.isSuccessful) {
                        response.body()
                            ?.filter { it.valor != null && !it.valor!!.isNaN() }
                            ?: emptyList()
                    } else emptyList()

                    val avgValue = if (response.isSuccessful) {
                        val validValues = response.body()
                            ?.mapNotNull { it.valor } // solo valores no nulos
                            ?.filter { !it.isNaN() }   // ignora NaN explícitos
                            ?: emptyList()

                        if (validValues.isNotEmpty()) validValues.average() else Double.NaN
                    } else {
                        Double.NaN
                    }

                    val avgByContaminant: Map<Int, Double> = validResponses
                        .groupBy { it.contaminant }
                        .mapValues { entry ->
                            val vals = entry.value.map { it.valor!! }
                            if (vals.isNotEmpty()) vals.average() else Double.NaN
                        }

                    tempMap[station.id] = avgValue
                    tempValuesMap[station.id] = avgByContaminant
                    //Log.d("Average", "Station ${station.id} → avg = $avgValue")

                } catch (e: Exception) {
                    tempMap[station.id] = Double.NaN
                    tempValuesMap[station.id] = emptyMap()
                    Log.e("MapViewModel", "Error al obtener promedio para estación ${station.id}", e)
                }
            }
            _averageMap.putAll(tempMap)
            _valuesMap.putAll(tempValuesMap)

            Log.d("Conts", "${tempValuesMap}")
            Log.d("Testing", "averageMap: ${averageMap}")
            onComplete()
        }
    }

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
