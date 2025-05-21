package com.front_pes.features.screens.xamistat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import kotlinx.coroutines.launch
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloqueigViewModel: ViewModel() {

    data class usuarisBlock(val id: Int, val id_correu_usuari: String, val id_bloqueig: Int)
    /*
    VARAIBLE PER PODER OBTENIR TOTS ELS MEMBRES BLOQUEJATS
     */
    var usuaris_bloquejats by mutableStateOf<List<usuarisBlock>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init{
        get_all_bloquejats()
    }
    fun get_all_bloquejats()=viewModelScope.launch {
        _isLoading.value = true;
        try{
            val response = RetrofitClient.apiService.get_all_bloqueigs_usuari(CurrentUser.correu)
            usuaris_bloquejats = response.map {
                item -> usuarisBlock(id = item.id, id_correu_usuari = item.bloquejat, id_bloqueig = item.id)
            }
            _isLoading.value = false;
        } catch (e:Exception){
            println("Error al obtenir tots els usuaris bloquejats del teu compte: ${e.message}")
            _isLoading.value = false;
        }
    }
    fun delete_bloqueig(BlockId: Int)= viewModelScope.launch {
        _isLoading.value = true;
        try{
            RetrofitClient.apiService.eliminar_bloqueig(BlockId)
            get_all_bloquejats()
            _isLoading.value = false;
        } catch(e:Exception){
            println("Error al eliminar el bloqueig d'aquest usuari: ${e.message}")
            _isLoading.value = false;
        }
    }
}