// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.map

data class EstacioQualitatAireResponse(
    val id: Int,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double,
    val index_qualitat_aire: Double,
    val nom_estacio: String,
    val descripcio: String?
)