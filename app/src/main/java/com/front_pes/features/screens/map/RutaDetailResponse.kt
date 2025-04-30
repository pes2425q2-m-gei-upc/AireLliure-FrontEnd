package com.front_pes.features.screens.map

data class RutaDetailResponse(
    val id: Int,
    val descripcio: String,
    val nom: String,
    val dist_km: Float,
    val punt_inici: punts?=null,
    val valoracions: Array<valoracions>?=null
)

data class punts(
    val latitud: Int,
    val longitud: Int
)

data class valoracions(
    val id: Int,
    val puntuacio: Float,
    val comentari: String,
    val usuari: String,
    val ruta: Int,
    val nom_usuari: String
)