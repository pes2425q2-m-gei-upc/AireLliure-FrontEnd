package com.front_pes.features.screens.administrador

import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HabilitacionsViewModel : ViewModel() {
    data class detallUser(val correu: String, val nom: String)
    /*
    Variable deshabilitats
     */
    var deshabilitats by mutableStateOf<List<detallUser>>(emptyList())
    /*
    Variable de tots els habilitats
  */
    var habilitats by mutableStateOf<List<detallUser>>(emptyList())
    /*
    VARIABLE DE TOTS ELS USUARIS HABILITATS
     */
    var errorMessage by mutableStateOf<String?>(null)
    fun get_all_usuaris_habilitats()=viewModelScope.launch {
        try {
            val resposta = RetrofitClient.apiService.get_habilitats()
            habilitats = resposta.map { item ->  detallUser(correu = item.correu, nom = item.nom)}
        } catch(e:Exception){
            println("Error al carregar tots els usuaris habilitats de l'APP: ${e.message}")
        }
    }
    fun get_all_usuaris_deshabilitats()= viewModelScope.launch {
        try {
            val respost = RetrofitClient.apiService.get_deshabilitats()
            deshabilitats = respost.map { item -> detallUser(correu = item.correu, nom = item.nom)}
        } catch(e:Exception){
            println("Error al carregar els usuaris deshabilitats de l'APP: ${e.message}")
        }
    }
    fun deshabilitar(correu: String)=viewModelScope.launch {
        try{
            val resp = RetrofitClient.apiService.deshabilitar(CurrentUser.correu, correu)
            get_all_usuaris_habilitats()
            get_all_usuaris_deshabilitats()
        } catch(e:Exception){
            println("Error al executar la funcio de deshabilitar: ${e.message}")
        }
    }
    fun rehabilitar(correu: String)= viewModelScope.launch {
        try{
            val respo = RetrofitClient.apiService.rehabilitar(correu)
            get_all_usuaris_habilitats()
            get_all_usuaris_deshabilitats()
        } catch(e:Exception){
            println("Error al rehabilitar l'usuari : ${e.message}")
        }
    }
}