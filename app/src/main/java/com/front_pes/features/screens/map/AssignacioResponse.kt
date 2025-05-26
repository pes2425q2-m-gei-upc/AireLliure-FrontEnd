package com.front_pes.features.screens.map

import com.google.gson.annotations.SerializedName

data class DificultatResponse(
    val id: Int,
    val usuari: String,
    val ruta: Int,
    @SerializedName("dificultat")
    val dificultat: String
)

data class AccessibilitatResponse(
    val id: Int,
    val usuari: String,
    val ruta: Int,
    @SerializedName("accesibilitat")
    val accesibilitat: String
)