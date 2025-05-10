package com.front_pes.features.screens.ActivitatsEvents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Date


class eventViewModel: ViewModel() {

    /*
    VARIABLE DE ACTIVITATS PUBLIQUES
     */
    var events by mutableStateOf<List<ActivityResponse>>(emptyList())
    /*
    VARAIBLE DE DETALL DE UNA ACTIVITAT
     */
    var event by mutableStateOf<ActivityResponse?>(null)
    /*
    VARIABLE DE MISSATGES D'ERROR
     */
    var errorMessage by mutableStateOf<String?>(null)

    fun get_all_publiques()=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.all_events()
            events = resposta
        } catch(e:Exception){
            println("Error al carregar les activitats publiques: ${e.message}")
        }
    }
    fun crear_activitat_event_public(nom:String, desc: String, data_inici:String, data_fi:String, limit:Int)=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.create_new_event(ActivityRequest(nom=nom, descripcio = desc, data_inici= data_inici, data_fi = data_fi, creador = CurrentUser.correu, limit = limit))
            get_all_publiques()
        }catch(e:Exception){
            println("Error al crear event public: ${e.message}")
        }
    }
    fun apuntarse_activitat(id_event: Int)=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.apuntarse(ApuntarseRequest(event = id_event, usuari = CurrentUser.correu))
        }catch(e:Exception){
            println("Error al apuntar-se: ${e.message}")
        }
    }
    fun eliminar_event(id: Int)=viewModelScope.launch {
        try{
            val resposta = RetrofitClient.apiService.eliminar_event_public(id)
            get_all_publiques()
        }catch(e:Exception){
            println("Error al eliminar event: ${e.message}")
        }
    }
    fun get_by_id(id:Int)=viewModelScope.launch {
        try{
           val resposta = RetrofitClient.apiService.get_event_by_id(id)
            event = resposta
        }catch(e:Exception){
            println("Error al carregar el model")
        }
    }
}