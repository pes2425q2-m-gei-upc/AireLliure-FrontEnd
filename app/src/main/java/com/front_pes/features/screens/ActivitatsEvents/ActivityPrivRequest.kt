package com.front_pes.features.screens.ActivitatsEvents

import com.google.gson.annotations.SerializedName

data class ActivityPrivRequest(
    val nom: String,
    @SerializedName("descripció")val descripcio: String,
    val data_inici: String, // en formato ISO ha de estar para el datetime
    val data_fi: String, // en formato ISO ha de estar para el datetime.
    @SerializedName("creador_event")val creador: String,
    @SerializedName("xat_event")val xat: Int
)
