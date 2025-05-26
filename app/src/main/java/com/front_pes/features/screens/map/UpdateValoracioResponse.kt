// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.map

data class UpdateValoracioResponse(
    val id: Int,
    val puntuacio: Float,
    val comentari: String,
    val usuari: String,
    val ruta: Int,
    val nom_usuari: String
)