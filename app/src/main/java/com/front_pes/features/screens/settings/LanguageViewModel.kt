// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.front_pes.utils.LanguagePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val languagePreferences = LanguagePreferences(application)

    private val _selectedLanguage = MutableStateFlow("es") // Español por defecto
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    init {
        // Cargar el idioma guardado al iniciar la app
        viewModelScope.launch {
            _selectedLanguage.value = languagePreferences.selectedLanguage.first()
        }
    }

    // Cambiar idioma y guardarlo
    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            _selectedLanguage.value = languageCode
            languagePreferences.saveLanguage(languageCode)
        }
    }
}
