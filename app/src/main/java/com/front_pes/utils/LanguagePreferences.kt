@file:Suppress("detekt")
package com.front_pes.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crear DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

class LanguagePreferences(private val context: Context) {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")

    // Leer idioma guardado
    val selectedLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "es" // Español por defecto
    }

    // Guardar idioma seleccionado
    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}
