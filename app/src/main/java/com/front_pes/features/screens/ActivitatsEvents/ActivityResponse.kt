// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.ActivitatsEvents

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ActivityResponse(
    val id: Int,
    val nom: String,
    @SerializedName("descripció")val descripcio: String,
    val data_inici: String,
    val data_fi: String,
    val creador_event: String,
    val xat: Int?=null,
    val limit: Int?=null
)