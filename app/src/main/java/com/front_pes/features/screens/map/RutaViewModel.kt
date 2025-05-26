// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.map

import android.telecom.Call
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Response

class RutaViewModel : ViewModel() {
    var all_info_ruta by mutableStateOf<RutaDetailResponse?>(null)
    var valoracions by mutableStateOf<List<valoracions>>(emptyList())
    val mitjanaValoracions: Double
        get() = valoracions.map { it.puntuacio }.average().takeIf { it.isFinite() } ?: 0.0

    val nombreValoracions: Int
        get() = valoracions.size

    var dificultatRuta by mutableStateOf<String?>(null)
        private set

    var accesibilitatRuta by mutableStateOf<String?>(null)
        private set

    fun getAssignacionsRuta(rutaId: Int) = viewModelScope.launch {
        try {
            val dificultatList = RetrofitClient.apiService.getAssignacioEsportiva(rutaId)
            dificultatRuta = dificultatList.firstOrNull()?.dificultat

            val accesibilitatList = RetrofitClient.apiService.getAssignacioAccessibilitat(rutaId)
            accesibilitatRuta = accesibilitatList.firstOrNull()?.accesibilitat

        } catch (e: Exception) {
            println("Error carregant assignacions: ${e.message}")
        }
    }

    fun guardarClassificacio(dificultat: String, accesibilitat: String, rutaId: Int) = viewModelScope.launch {
        try {
            val difRequest = AssignacioDificultatRequest(usuari = CurrentUser.correu, ruta = rutaId, dificultat = dificultat)
            val accRequest = AssignacioAccessibilitatRequest(usuari = CurrentUser.correu, ruta = rutaId, accesibilitat = accesibilitat)

            println("Enviando dificultad: $difRequest")
            println("Enviando accesibilidad: $accRequest")

            val respDif = RetrofitClient.apiService.postAssignacioEsportiva(difRequest)
            val respAcc = RetrofitClient.apiService.postAssignacioAccessibilitat(accRequest)

            if (respDif.isSuccessful && respAcc.isSuccessful) {
                println("Classificació guardada correctament")
                getAssignacionsRuta(rutaId) // Recargar los valores tras guardar
            } else {
                println("Error al guardar classificació: ${respDif.code()} / ${respAcc.code()}")
                println("RespDif ErrorBody: ${respDif.errorBody()?.string()}")
                println("RespAcc ErrorBody: ${respAcc.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            println("Excepció al guardar classificació: ${e.localizedMessage}")
        }
    }
    /*
    fun guardarClassificacio(dificultat: String, accesibilitat: String) {
        dificultatRuta = dificultat
        accesibilitatRuta = accesibilitat
        println("Guardat -> Dificultat: $dificultat | Accessibilitat: $accesibilitat")
    }

     */

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

    suspend fun editarValoracio(
        valoracioOriginal: valoracions,
        nouComentari: String,
        novaPuntuacio: Float,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            val request = UpdateValoracioRequest(
                puntuacio = novaPuntuacio,
                comentari = nouComentari,
                usuari = valoracioOriginal.usuari,
                ruta = valoracioOriginal.ruta,
                nom_usuari = valoracioOriginal.nom_usuari
            )

            val response = RetrofitClient.apiService.updateValoracio(valoracioOriginal.id, request)

            if (response.isSuccessful) {
                onSuccess()
            } else {
                onError("Error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            onError("Exception: ${e.message}")
        }
    }

    suspend fun eliminarValoracio(id: Int): Boolean {
        return try {
            val resposta = RetrofitClient.apiService.deleteValoracio(id)
            resposta.isSuccessful
        } catch (e: Exception) {
            println("Error eliminant valoració: ${e.message}")
            false
        }
    }
}