package com.front_pes.features.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch

class RutaViewModel : ViewModel() {
    var all_info_ruta by mutableStateOf<RutaDetailResponse?>(null)
    var valoracions by mutableStateOf<List<valoracions>>(emptyList())
    val mitjanaValoracions: Double
        get() = valoracions.map { it.puntuacio }.average().takeIf { it.isFinite() } ?: 0.0

    val nombreValoracions: Int
        get() = valoracions.size
    fun get_informacio_ruta(id_ruta: Int)= viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.get_info_ruta(id_ruta)
            all_info_ruta = resposta.ruta
            valoracions = resposta.valoracions ?: emptyList()
        } catch(e:Exception){
            println("Error, no s'han carregat les dades correctament")
        }
    }
    fun afegir_valoracio(user_id: String, ruta_id: Int, puntuacio_:Float, comentari_: String)=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.afegir_valoracio(RutaAfegirValRequest(usuari = user_id, ruta = ruta_id, puntuacio = puntuacio_, comentari=comentari_))
            get_informacio_ruta(ruta_id)
        } catch(e:Exception){
            println("Error no s'ha pogut crear la valoracio")
        }
    }
}