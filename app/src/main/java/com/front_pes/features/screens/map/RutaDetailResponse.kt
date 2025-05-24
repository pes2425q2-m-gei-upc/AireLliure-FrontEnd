package com.front_pes.features.screens.map

import com.google.gson.annotations.SerializedName

data class RutaDetailResponse(
    val id: Int,
    val descripcio: String,
    val nom: String,
    val dist_km: Float,
    val punt_inici: Int?=null,
    @SerializedName("valoracions") val valoracions: Array<valoracions>?=null
)

data class RutaWrapperResponse(
    val ruta: RutaDetailResponse,
    val valoracions: List<valoracions>?
)

data class valoracions(
    val id: Int,
    val puntuacio: Float,
    val comentari: String,
    val usuari: String,
    val ruta: Int,
    val nom_usuari: String
)