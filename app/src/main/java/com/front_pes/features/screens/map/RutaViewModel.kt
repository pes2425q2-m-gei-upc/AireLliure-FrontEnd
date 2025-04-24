package com.front_pes.features.screens.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RutaViewModel : ViewModel() {

    private val _selectedRuta = MutableStateFlow<RutasResponse?>(null)
    val selectedRuta: StateFlow<RutasResponse?> = _selectedRuta

    fun setRuta(ruta: RutasResponse) {
        _selectedRuta.value = ruta
    }

    fun clearRuta() {
        _selectedRuta.value = null
    }
}