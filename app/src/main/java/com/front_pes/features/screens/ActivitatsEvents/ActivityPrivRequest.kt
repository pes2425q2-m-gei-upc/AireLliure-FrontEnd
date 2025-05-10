package com.front_pes.features.screens.ActivitatsEvents

data class ActivityPrivRequest(
    val nom: String,
    val descripcio: String,
    val data_inici: String, // en formato ISO ha de estar para el datetime
    val data_fi: String, // en formato ISO ha de estar para el datetime.
    val creador: String,
    val xat: Int
)
