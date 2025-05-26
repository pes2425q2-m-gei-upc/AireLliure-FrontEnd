@file:Suppress("detekt")
package com.front_pes.features.screens.xamistat

data class SolicitarAmistatRequest(
    val solicita: String,
    val accepta: String,
    val pendent: Boolean
)
