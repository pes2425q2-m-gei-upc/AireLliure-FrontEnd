package com.front_pes.features.screens.xamistat

import android.util.Log
import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.front_pes.CurrentUser
import com.front_pes.features.screens.login.LoginRequest
import com.front_pes.features.screens.login.LoginResponse
import com.front_pes.features.screens.xamistat.LlistaAmistatResponse
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LlistatAmistatViewModel: ViewModel() {

    data class AmistatLine(val idAmistat: Int, val id: String, val nom: String, val correu: String)
    data class UsuariLine(val id: String?=null, val nom: String?=null, val correu: String)
    /* VAR DEL LLISTAT DE AMICS */
    var llista_amics by mutableStateOf <List<AmistatLine>>(emptyList())
    /* VAR DE TOTS ELS USUARIS PER AL SELECTOR   */
    var all_users by mutableStateOf <List<UsuariLine>>(emptyList())
    /*VARIABLE DE PETICIONS REBUDES QUE ALTRES USUARIS ME HAN FET     */
    var all_rebudes by mutableStateOf<List<AmistatLine>>(emptyList())
    /* VARIABLE DE PETICIONS QUE JO HE FET A ALTRES USUARIS     */
    var all_enviades by mutableStateOf<List<AmistatLine>>(emptyList())
    /*  VAR PER ALS ERRORS */
    var errorMessage by mutableStateOf <String?>(null)

    init {
        getXatsAmics()
        get_rebudes()
        get_usuaris()
        get_enviades()
    }

    fun getXatsAmics() = viewModelScope.launch {
        try {
            val resposta = RetrofitClient.apiService.getAmistatUsuarybyCorreu(CurrentUser.correu)
            llista_amics = resposta.map { item ->
                AmistatLine(
                    idAmistat = item.idAmistat,
                    id = item.correu,
                    nom = item.nom,
                    correu = item.correu
                )
            }
        } catch (e: Exception) {
            errorMessage = "Error carregant el llistat d'amics: ${e.message}"
            println(errorMessage)
        }
    }


    fun get_usuaris() = viewModelScope.launch {
        try {
            val resposta = RetrofitClient.apiService.get_all_usuaris(CurrentUser.correu)
            all_users = resposta.map { item ->
                UsuariLine(
                    id = item.correu,
                    nom = item.nom,
                    correu = item.correu
                )
            }
        } catch (e: Exception) {
            errorMessage = "Error al carregar els usuaris: ${e.message}"
            println(errorMessage)
        }
    }


    fun get_rebudes() = viewModelScope.launch {
        try {
            val resposta = RetrofitClient.apiService.get_all_rebudes(CurrentUser.correu)
            all_rebudes = resposta.map { item ->
                AmistatLine(
                    idAmistat = item.id,
                    id = item.solicita,
                    nom = item.nom,
                    correu = item.solicita
                )
            }
        } catch (e: Exception) {
            errorMessage = "Error al carregar les peticions rebudes: ${e.message}"
            println(errorMessage)
        }
    }


    fun get_enviades() = viewModelScope.launch {
        try {
            val resposta = RetrofitClient.apiService.get_all_envaides(CurrentUser.correu)
            all_enviades = resposta.map { item ->
                AmistatLine(
                    idAmistat = item.id,
                    id = item.solicita,
                    nom = item.nom,
                    correu = item.accepta ?: ""
                )
            }
        } catch (e: Exception) {
            errorMessage = "Error al carregar les peticions enviades: ${e.message}"
            println(errorMessage)
        }
    }


    fun seguir_usuari(accepta: String) = viewModelScope.launch {
        try {
            val body = SolicitarAmistatRequest(
                solicita = CurrentUser.correu,
                accepta = accepta,
                pendent = true
            )
            RetrofitClient.apiService.create_new_amistat(body)
            getXatsAmics()
            get_rebudes()
            get_usuaris()
            get_enviades()

        } catch (e: Exception) {
            errorMessage = "Error en seguir usuari: ${e.message}"
            println(errorMessage)
        }
    }

    fun cancelar_solicitud_enviada(AmistatId: Int) = viewModelScope.launch {
        try {
            RetrofitClient.apiService.delete_amistat(AmistatId)
            getXatsAmics()
            get_rebudes()
            get_usuaris()
            get_enviades()

        } catch (e: Exception) {
            errorMessage = "Error en cancel·lar la solicitud enviada: ${e.message}"
            println(errorMessage)
        }
    }

    fun cancelar_solicitud_rebuda(AmistatId: Int) = viewModelScope.launch {
        try {
            RetrofitClient.apiService.delete_amistat(AmistatId)
            getXatsAmics()
            get_rebudes()
            get_usuaris()
            get_enviades()

        } catch (e: Exception) {
            errorMessage = "Error en cancel·lar la solicitud enviada: ${e.message}"
            println(errorMessage)
        }
    }

    fun aceptar_solicitud_rebuda(AmistatId: Int) = viewModelScope.launch {
        try {
            val body_enviar = mapOf("pendent" to false)
            RetrofitClient.apiService.update_amistat(AmistatId, body_enviar)
            getXatsAmics()
            get_rebudes()
            get_usuaris()
            get_enviades()
        } catch (e: Exception) {
            errorMessage = "Error en acceptar la solicitud: ${e.message}"
            println(errorMessage)
        }
    }

    fun delete_amistad(AmistatId: Int)=viewModelScope.launch {
        try{
            RetrofitClient.apiService.delete_amistat(AmistatId)
            getXatsAmics()
            get_rebudes()
            get_usuaris()
            get_enviades()
        } catch(e:Exception){
            println("Error al eliminar la amistad: ${e.message}")
        }
    }
}

