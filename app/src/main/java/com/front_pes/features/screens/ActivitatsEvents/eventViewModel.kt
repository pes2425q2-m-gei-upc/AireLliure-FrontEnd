// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.ActivitatsEvents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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

    var participacions by mutableStateOf<List<ActivityResponse>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var apuntadoCorrectamente by mutableStateOf(false)
        private set
    init {
        get_all_publiques()
        get_participacions()
    }

    fun get_all_publiques()=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta = RetrofitClient.apiService.all_events()
            events = resposta
            _isLoading.value = false;
        } catch(e:Exception){
            println("Error al carregar les activitats publiques: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun crear_activitat_event_public(nom:String, desc: String, data_inici:String, data_fi:String, limit:Int)=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta = RetrofitClient.apiService.create_new_event(
                ActivityRequest(
                    nom = nom,
                    descripcio = desc,
                    data_inici = data_inici,
                    data_fi = data_fi,
                    creador = CurrentUser.correu,
                    limit = limit
                )
            )
            if (resposta.isSuccessful) {
                println("ENVIADOOOOOUUUUUUUUUUU")
                get_all_publiques()
                get_participacions()
            } else {
                println("Error al crear event public: código ${resposta.code()}, errorBody: ${resposta.errorBody()?.string()}")
            }
            get_all_publiques()
        }catch(e:Exception){
            println("Error al crear event public: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun apuntarse_activitat(id_event: Int,  onSuccess: () -> Unit = {})=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta = RetrofitClient.apiService.apuntarse(ApuntarseRequest(event = id_event, usuari = CurrentUser.correu))
            apuntadoCorrectamente = true
            onSuccess() // Esto cerrará el diálogo
            get_all_publiques()
            get_participacions()
        }catch(e:Exception){
            println("Error al apuntar-se: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun eliminar_event(id: Int)=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta = RetrofitClient.apiService.eliminar_event_public(id)
            get_all_publiques()
            get_participacions()
        }catch(e:Exception){
            println("Error al eliminar event: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun get_participacions()=viewModelScope.launch {
        _isLoading.value = true;
        try {
            val resposta = RetrofitClient.apiService.get_on_participo(CurrentUser.correu)
            participacions = resposta
            println("YEHHHHHHHHHHHHHHHHHHHHHAAAAAAA2")
            println(participacions)
            _isLoading.value = false;
        } catch(e:Exception){
            println("error al agafar les teves participacions")
            _isLoading.value = false;
        }
    }
    fun abandonar(id_event:Int,  onSuccess: () -> Unit = {})=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta = RetrofitClient.apiService.eliminar_participacio(CurrentUser.correu,id_event)
            if(resposta.isSuccessful){
                print(resposta.body())
            } else {
                println("Error al abandonar: código ${resposta.code()}, errorBody: ${resposta.errorBody()?.string()}")
            }
            get_all_publiques()
            get_participacions()
            onSuccess()
        } catch(e:Exception){
            println("Error al abandonar event: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun editar_event(event_id: Int, nom:String, desc: String, data_inici:String, data_fi:String, limit:Int)=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val resposta =RetrofitClient.apiService.editar_event_public(event_id, ActivityRequest(
                nom = nom,
                descripcio = desc,
                data_inici = data_inici,
                data_fi = data_fi,
                creador = CurrentUser.correu,
                limit = limit
            ))
            get_all_publiques()
            get_participacions()
            if(resposta.isSuccessful){
                println("Yupi!!")
            } else {
                println("Error al abandonar: código ${resposta.code()}, errorBody: ${resposta.errorBody()?.string()}")
                _isLoading.value = false;
            }
        }catch(e:Exception){
            println("Error al editar l'event: ${e.message}")
            _isLoading.value = false;
        }
    }
}