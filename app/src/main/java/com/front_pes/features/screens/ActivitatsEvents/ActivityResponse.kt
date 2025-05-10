package com.front_pes.features.screens.ActivitatsEvents

import java.util.Date

data class ActivityResponse(
    val id: Int,
    val nom: String,
    val descripcio: String,
    val data_inici: String,
    val data_fi: String,
    val creador: Int,
    val xat: Int?=null,
    val limit: Int?=null
)
