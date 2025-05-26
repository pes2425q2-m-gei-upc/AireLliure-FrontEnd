@file:Suppress("detekt")
package com.front_pes.features.screens.map

data class ActivitatCulturalResponse(
    val id: Int,
    val latitud: Double,
    val longitud: Double,
    val index_qualitat_aire: Int,
    val nom_activitat: String,
    val descripcio: String,
    val data_inici: String,
    val data_fi: String
)