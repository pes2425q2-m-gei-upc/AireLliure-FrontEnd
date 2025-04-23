package com.front_pes.features.screens.Ranking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingViewModel : ViewModel() {

    data class ranking(val name: String, val points: Int)

    /* VARAIBLE DE REBUDA DE DADES PER ALL*/
    var ranking_all_users by mutableStateOf<List<ranking>>(emptyList())

    /* VARAIBLE DE REBUDA DE DADES PER QUAN SIGUI NOMÉS AMICS*/
    var ranking_amics by mutableStateOf<List<ranking>>(emptyList())

    /* VARAIBLE DE ERRORS */
    var errorMessage by mutableStateOf<String?>("")

    init {
        ranking_tt_users()
        ranking_n_amics()
    }

    fun ranking_tt_users() = viewModelScope.launch {
        try {
            val call = RetrofitClient.apiService.get_all_ranking()
            call.enqueue(object : Callback<List<RankingResponse>> {
                override fun onResponse(
                    call: Call<List<RankingResponse>>,
                    response: Response<List<RankingResponse>>
                ) {
                    if (response.code() == 200) {
                        val resposta = response.body()
                        resposta?.let {
                            ranking_all_users = it.map { item -> ranking(
                                name = item.nom,
                                points = item.punts
                            ) }
                        }
                    } else {
                        errorMessage = when (response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                    errorMessage = "Error al carregar el Ranking: ${t.message}"
                }
            })
        } catch (e: Exception) {
            println("Error al intentar carregar el ranking: ${e.message}")
        }
    }

    fun ranking_n_amics() = viewModelScope.launch {
        try {
            val call = RetrofitClient.apiService.get_ranking_amistats(CurrentUser.correu)
            call.enqueue(object : Callback<List<RankingResponse>> {

                override fun onResponse(
                    call: Call<List<RankingResponse>>,
                    response: Response<List<RankingResponse>>
                ) {
                    if (response.code() == 200) {
                        val resposta = response.body()
                        resposta?.let {
                            ranking_amics = it.map { item -> ranking(
                                name = item.nom,
                                points = item.punts
                            ) }
                        }
                    } else {
                        errorMessage = when (response.code()) {
                            404 -> "usuari no existeix"
                            401 -> "contrasenya incorrecta"
                            else -> "Error desconocido: ${response.code()}"
                        }
                    }
                }
                override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                    println("Error al carregar el ranking d'amics: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("Error en carregar el ranking d'amics: ${e.message}")
        }
    }
}
