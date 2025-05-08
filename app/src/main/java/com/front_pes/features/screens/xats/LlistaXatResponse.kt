package com.front_pes.features.screens.xats

data class LlistaXatResponse(
    val id: Int,
    val nom: String,
    val usuari1: String?=null,
    val usuari2: String?=null,
    val descripcio: String?=null,
    val creador: String?=null,
    val membres: List<String>?=null
)

typealias LlistatXatsResponse = List<LlistaXatResponse>