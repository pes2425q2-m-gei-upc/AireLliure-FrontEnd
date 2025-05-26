@file:Suppress("detekt")
package com.front_pes.features.screens.xamistat

data class SolicitarAmistatResponse(
    val id: Int,
    val solicita: String,
    val accepta: String?,
    val data_inici: String,
    val data_final: String?,
    val pendent: Boolean,
    val nom: String,
    val imatge: String?=null
)
