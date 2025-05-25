package com.front_pes.features.screens.map

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.SelectedContaminants
import com.front_pes.features.screens.user.UpdateProfileRequest
import com.front_pes.features.screens.user.UpdateProfileResponse
import com.front_pes.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext


class MapViewModel : ViewModel() {

    private val _averageMap = mutableStateMapOf<Int, Double>()
    val averageMap: Map<Int, Double> get() = _averageMap

    private val _valuesMap = mutableStateMapOf<Int, Map<Int, Double>>()
    val valuesMap: Map<Int, Map<Int, Double>> get() = _valuesMap

    var totalDistance by mutableStateOf(0f)
    var targetDistance by mutableStateOf(0f)
    var isTracking by mutableStateOf(false)
    var nomRutaRecorreguda by mutableStateOf("")
    val rutaFinalitzada by derivedStateOf { totalDistance >= targetDistance }
    //val rutaFinalitzada = true
    var detenerRuta = false;
    var trackingStartTime by mutableStateOf<Long?>(null)
    var elapsedTime by mutableStateOf(0L) // en milisegundos
    private var timerJob: Job? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun startTracking() {
        isTracking = true
        trackingStartTime = System.currentTimeMillis()
        startTimer()
        detenerRuta = false;
    }

    fun stopTracking(context: Context) {
        isTracking = false
        trackingStartTime?.let {
            elapsedTime += System.currentTimeMillis() - it
        }
        trackingStartTime = null
        stopTimer()
        detenerRuta = true;

        val velocitatPromitja = totalDistance / (elapsedTime/1000f)
        val velocitatValida = velocitatPromitja <= 16
        //val velocitatValida = false
        if (rutaFinalitzada && detenerRuta) {
            if (velocitatValida) {
                rewardUser(context)
            }
            else {
                Toast.makeText(context, "Ruta fraudulenta :/", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                trackingStartTime?.let {
                    elapsedTime = System.currentTimeMillis() - it
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun rewardUser(context: Context) {
        var puntsAfegits = targetDistance.toInt()
        val nousPunts = ((CurrentUser.punts ?: 0) + puntsAfegits)
        val request = UpdateProfileRequest(punts = nousPunts)

        val call = RetrofitClient.apiService.updateProfile(CurrentUser.correu, request)

        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    if (updatedUser != null) {
                        CurrentUser.punts = updatedUser.punts
                        Toast.makeText(context, "Has guanyat $puntsAfegits punts!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error al actualitzar punts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(context, "Error de xarxa", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun fetchAveragesForStations(
        stations: List<EstacioQualitatAireResponse>,
        onComplete: () -> Unit = {}
    ) {
        _isLoading.value = true;
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
            _isLoading.value = false;
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
