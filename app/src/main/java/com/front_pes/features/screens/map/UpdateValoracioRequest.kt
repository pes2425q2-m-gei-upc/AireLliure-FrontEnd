@file:Suppress("detekt")
package com.front_pes.features.screens.map

data class UpdateValoracioRequest(
    val puntuacio: Float,
    val comentari: String,
    val usuari: String,
    val ruta: Int,
    val nom_usuari: String
)