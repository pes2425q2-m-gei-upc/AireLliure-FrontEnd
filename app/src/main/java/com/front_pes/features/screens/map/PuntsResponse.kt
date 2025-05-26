@file:Suppress("detekt")
package com.front_pes.features.screens.map

data class PuntsResponse(
    val id: Int,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double,
    val index_qualitat_aire: Double
)