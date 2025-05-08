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

    var dificultatEsportiva by mutableStateOf("")
        private set

    var accesibilitatRespiratoria by mutableStateOf("")
        private set

    fun guardarClassificacio(dificultat: String, accesibilitat: String) {
        dificultatEsportiva = dificultat
        accesibilitatRespiratoria = accesibilitat
        println("Guardat -> Dificultat: $dificultat | Accessibilitat: $accesibilitat")
    }

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
    fun extreureDistanciaDescripcio(): String {
        val descripcio = all_info_ruta?.descripcio ?: return "Desconeguda"
        val regex = Regex("""Distància:\s*([\d.,]+)\s*m""", RegexOption.IGNORE_CASE)
        val match = regex.find(descripcio)
        return match?.groups?.get(1)?.value ?: "Desconeguda"
    }
    fun obtenirItinerariAmbDescripcio(): String {
        val html = all_info_ruta?.descripcio ?: return "Desconeguda"
        val regex = Regex("<p>(.*?)</p>", RegexOption.IGNORE_CASE)
        val paragrafs = regex.findAll(html).map { it.groupValues[1] }.toList()

        val itinerari = paragrafs.getOrNull(0)?.removePrefix("Itinerari: ") ?: ""
        val descripcio = paragrafs.getOrNull(2) ?: ""

        return listOf(itinerari, descripcio)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }
}