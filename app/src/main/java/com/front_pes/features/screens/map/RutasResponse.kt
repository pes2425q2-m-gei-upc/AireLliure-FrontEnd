package com.front_pes.features.screens.map

data class RutasResponse(
    val id: String,
    val descripcio: String,
    val nom: String,
    val dist_km: Double,
    val punt_inici: Int?
)