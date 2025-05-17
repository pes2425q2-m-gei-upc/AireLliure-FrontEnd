package com.front_pes.features.screens.map

import com.google.gson.annotations.SerializedName

data class AssignacioDificultatRequest(
    val usuari: String,
    val ruta: Int,
    @SerializedName("dificultat")
    val dificultat: String
)

data class AssignacioAccessibilitatRequest(
    val usuari: String,
    val ruta: Int,
    @SerializedName("accesibilitat")
    val accesibilitat: String
)