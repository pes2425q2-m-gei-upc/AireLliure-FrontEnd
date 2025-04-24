package com.front_pes.features.screens.xamistat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.CurrentUser
import com.front_pes.network.RetrofitClient
import kotlinx.coroutines.launch

class BloqueigViewModel : ViewModel() {

    data class usuarisBlock(val id: Int, val id_correu_usuari: String, val id_bloqueig: Int)

    /*
    VARAIBLE PER PODER OBTENIR TOTS ELS MEMBRES BLOQUEJATS
     */
    var usuaris_bloquejats by mutableStateOf<List<usuarisBlock>>(emptyList())

    init {
        get_all_bloquejats()
    }
    fun get_all_bloquejats() = viewModelScope.launch {
        try {
            val response = RetrofitClient.apiService.get_all_bloqueigs_usuari(CurrentUser.correu)
            usuaris_bloquejats = response.map {
                    item ->
                usuarisBlock(
                    id = item.id,
                    id_correu_usuari = item.bloquejat,
                    id_bloqueig = item.id
                )
            }
        } catch (e: Exception) {
            println("Error al obtenir tots els usuaris bloquejats del teu compte: ${e.message}")
        }
    }
    fun delete_bloqueig(BlockId: Int) = viewModelScope.launch {
        try {
            RetrofitClient.apiService.eliminar_bloqueig(BlockId)
            get_all_bloquejats()
        } catch (e: Exception) {
            println("Error al eliminar el bloqueig d'aquest usuari: ${e.message}")
        }
    }
}
