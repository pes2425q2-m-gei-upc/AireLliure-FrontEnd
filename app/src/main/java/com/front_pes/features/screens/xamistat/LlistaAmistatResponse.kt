package com.front_pes.features.screens.xamistat

data class LlistaAmistatResponse(
    val idAmistat: Int,
    val correu: String,
    val nom: String,
    val about: String,
    val punts: Int,
    val imatge: String?=null
)
