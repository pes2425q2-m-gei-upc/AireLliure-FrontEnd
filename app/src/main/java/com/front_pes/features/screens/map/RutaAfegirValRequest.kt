@file:Suppress("detekt")
package com.front_pes.features.screens.map

data class RutaAfegirValRequest(
    val usuari: String,
    val ruta: Int,
    val puntuacio: Float,
    val comentari: String
)